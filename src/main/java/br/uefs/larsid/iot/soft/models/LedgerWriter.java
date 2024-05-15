package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.models.transactions.IndexTransaction;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 *
 * @author Allan Capistrano
 */
public class LedgerWriter implements Runnable {

  /*-------------------------Constantes---------------------------------------*/
  private static final long SLEEP = 5000;
  private static final String ENDPOINT = "message";
  /*--------------------------------------------------------------------------*/

  private Thread ledgerWriter;
  private boolean debugModeValue;
  private String urlApi;
  private final BlockingQueue<IndexTransaction> DLTOutboundBuffer;

  private static final Logger logger = Logger.getLogger(
    LedgerWriter.class.getName()
  );

  public LedgerWriter(
    String protocol,
    String url,
    int port,
    int bufferSize,
    boolean debugModeValue
  ) {
    this.urlApi = String.format("%s://%s:%s", protocol, url, port);
    this.debugModeValue = debugModeValue;
    this.DLTOutboundBuffer =
      new ArrayBlockingQueue<IndexTransaction>(bufferSize);

    if (this.ledgerWriter == null) {
      this.ledgerWriter = new Thread(this);
      this.ledgerWriter.setName("TANGLE_MONITOR/LEDGER_WRITER");
      this.ledgerWriter.start();
    }
  }

  /**
   * Adiciona uma mensagem para ser publicada na Tangle Hornet.
   *
   * @param indexTransaction IndexTransaction - Transação que será publicada.
   */
  public void put(IndexTransaction indexTransaction)
    throws InterruptedException {
    this.DLTOutboundBuffer.put(indexTransaction);
  }

  /**
   * Cria uma nova mensagem na Tangle Hornet.
   *
   * @param index String - Índice da mensagem.
   * @param data String - Conteúdo da mensagem.
   */
  private void createMessage(String index, String data) {
    try {
      URL url = new URL(String.format("%s/%s", this.urlApi, ENDPOINT));

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      String requestBody = String.format(
        "{\"index\": \"%s\",\"data\": %s}",
        index,
        data
      );

      try (
        DataOutputStream outputStream = new DataOutputStream(
          connection.getOutputStream()
        )
      ) {
        outputStream.writeBytes(requestBody);
        outputStream.flush();
      }

      int responseCode = connection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream())
        );
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        if (debugModeValue) {
          logger.info("API response: " + response.toString());
        }
      } else {
        if (debugModeValue) {
          logger.info("Error in HTTP request: " + responseCode);
        }
      }

      connection.disconnect();
    } catch (IOException ioe) {
      if (debugModeValue) {
        logger.severe(ioe.getMessage());
      }
    }
  }

  @Override
  public void run() {
    Gson gson = new Gson();

    while (!this.ledgerWriter.isInterrupted()) {
      try {
        long start = System.currentTimeMillis();
        IndexTransaction indexTransaction = this.DLTOutboundBuffer.take();

        indexTransaction
          .getTransaction()
          .setPublishedAt(System.currentTimeMillis());

        String transactionJson = gson.toJson(indexTransaction.getTransaction());

        this.createMessage(indexTransaction.getIndex(), transactionJson);
        long end = System.currentTimeMillis();
        logger.info("API Response time (ms): " + (end - start));

        Thread.sleep(SLEEP);
      } catch (InterruptedException ex) {
        this.ledgerWriter.interrupt();
      }
    }
  }
}
