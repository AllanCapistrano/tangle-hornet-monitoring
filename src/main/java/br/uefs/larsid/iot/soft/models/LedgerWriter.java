package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;
import br.uefs.larsid.iot.soft.models.transactions.Evaluation;
import br.uefs.larsid.iot.soft.models.transactions.IndexTransaction;
import br.uefs.larsid.iot.soft.models.transactions.Transaction;
import br.uefs.larsid.iot.soft.utils.CsvWriter;
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
 * @version 1.1.0
 */
public class LedgerWriter implements Runnable {

  /*---------------------------- Constantes ----------------------------------*/
  private static final long SLEEP = 5000;
  private static final String ENDPOINT = "message";
  private static String[] CSV_HEADER = { "Time (s)", "Response Time (ms)" };
  /*--------------------------------------------------------------------------*/

  /*----------------------------- CSV ----------------------------------------*/
  private String[] csvData = new String[2];
  private int csvIndex;
  private final CsvWriter csvWriter;
  /*--------------------------------------------------------------------------*/

  private Thread ledgerWriter;
  private boolean debugModeValue;
  private String urlApi;
  private final String index;
  private final BlockingQueue<IndexTransaction> DLTOutboundBuffer;

  private static final Logger logger = Logger.getLogger(
    LedgerWriter.class.getName()
  );

  public LedgerWriter(
    String protocol,
    String url,
    int port,
    int bufferSize,
    String index,
    CsvWriter csvWriter,
    boolean debugModeValue
  ) {
    this.urlApi = String.format("%s://%s:%s", protocol, url, port);
    this.index = index;
    this.debugModeValue = debugModeValue;
    this.DLTOutboundBuffer =
      new ArrayBlockingQueue<IndexTransaction>(bufferSize);

    this.csvWriter = csvWriter;
    this.csvIndex = 0;

    this.csvWriter.writeData(CSV_HEADER);

    if (this.ledgerWriter == null) {
      this.ledgerWriter = new Thread(this);
      this.ledgerWriter.setName("TANGLE_MONITOR/LEDGER_WRITER");
      this.ledgerWriter.start();
    }
  }

  public LedgerWriter(String urlApi, String index, boolean debugModeValue) {
    this.urlApi = urlApi;
    this.index = index;
    this.debugModeValue = debugModeValue;
    this.DLTOutboundBuffer = new ArrayBlockingQueue<IndexTransaction>(128);
    this.csvWriter = null;
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
  public void createMessage(String index, String data) {
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

        Transaction transaction = new Evaluation(
          "fakeSource",
          "fakeTarget",
          TransactionType.REP_EVALUATION,
          0,
          start,
          start
        );

        this.put(new IndexTransaction(this.index, transaction));

        IndexTransaction indexTransaction = this.DLTOutboundBuffer.take();

        indexTransaction
          .getTransaction()
          .setPublishedAt(System.currentTimeMillis());

        String transactionJson = gson.toJson(indexTransaction.getTransaction());

        this.createMessage(indexTransaction.getIndex(), transactionJson);
        long end = System.currentTimeMillis();
        long responseTime = end - start;

        logger.info("API write operation response time (ms): " + responseTime + "\n");

        if (this.csvWriter != null) {
          this.csvData[0] = String.valueOf(SLEEP / 1000 * this.csvIndex);
          this.csvData[1] = String.valueOf(responseTime);

          this.csvWriter.writeData(this.csvData);

          this.csvIndex++;
        }

        Thread.sleep(SLEEP);
      } catch (InterruptedException ex) {
        this.ledgerWriter.interrupt();
      }
    }
  }
}
