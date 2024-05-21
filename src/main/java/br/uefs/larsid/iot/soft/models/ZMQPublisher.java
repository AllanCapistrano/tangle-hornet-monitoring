package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;
import br.uefs.larsid.iot.soft.models.transactions.Evaluation;
import br.uefs.larsid.iot.soft.models.transactions.IndexTransaction;
import br.uefs.larsid.iot.soft.models.transactions.Transaction;
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
 * @version 1.0.0
 */
public class ZMQPublisher implements Runnable {

  /*---------------------------- Constantes ----------------------------------*/
  public static final long SLEEP = 5000;
  private static final String ENDPOINT = "message";
  /*--------------------------------------------------------------------------*/

  private String urlApi;
  private Thread zmqPublisher;
  private boolean debugModeValue;
  private final BlockingQueue<IndexTransaction> DLTOutboundBuffer;

  private static final Logger logger = Logger.getLogger(
    ZMQPublisher.class.getName()
  );

  public ZMQPublisher(
    String protocol,
    String url,
    int port,
    int bufferSize,
    boolean debugModeValue
  ) {
    this.urlApi = String.format("%s://%s:%s", protocol, url, port);
    this.DLTOutboundBuffer =
      new ArrayBlockingQueue<IndexTransaction>(bufferSize);

    this.debugModeValue = debugModeValue;

    if (this.zmqPublisher == null) {
      this.zmqPublisher = new Thread(this);
      this.zmqPublisher.setName("TANGLE_MONITOR/ZMQ_PUBLISHER");
      this.zmqPublisher.start();
    }
  }

  @Override
  public void run() {
    Gson gson = new Gson();

    while (!this.zmqPublisher.isInterrupted()) {
      try {
        Transaction transaction = new Evaluation(
          "fakeSourceZMQ",
          "fakeTargetZMQ",
          TransactionType.REP_ZMQ_MONITOR,
          0,
          System.currentTimeMillis(),
          System.currentTimeMillis()
        );

        this.put(
            new IndexTransaction(
              TransactionType.REP_ZMQ_MONITOR.toString(),
              transaction
            )
          );

        IndexTransaction indexTransaction = this.DLTOutboundBuffer.take();

        indexTransaction
          .getTransaction()
          .setPublishedAt(System.currentTimeMillis());

        String transactionJson = gson.toJson(indexTransaction.getTransaction());

        this.createMessage(indexTransaction.getIndex(), transactionJson);

        Thread.sleep(SLEEP);
      } catch (InterruptedException ex) {
        this.zmqPublisher.interrupt();
      }
    }
  }

  /**
   * Put a transaction to be published on Tangle Hornet
   *
   * @param indexTransaction IndexTransaction - Transação que será publicada.
   */
  public void put(IndexTransaction indexTransaction)
    throws InterruptedException {
    this.DLTOutboundBuffer.put(indexTransaction);
  }

  /**
   * Create a new message in Tangle Hornet.
   *
   * @param index String - Index of the message.
   * @param data String - Data of the message.
   */
  public void createMessage(String index, String data) {
    try {
      URL url = new URL(String.format("%s/%s", this.urlApi, ENDPOINT));

      /* Open HTTP connection. */
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true); // Permitir a escrita no corpo da requisição

      String requestBody = String.format(
        "{\"index\": \"%s\",\"data\": %s}",
        index,
        data
      );

      /* Writes request body to OutputStream */
      try (
        DataOutputStream outputStream = new DataOutputStream(
          connection.getOutputStream()
        )
      ) {
        outputStream.writeBytes(requestBody);
        outputStream.flush();
      }

      /* Receives the response */
      int responseCode = connection.getResponseCode();

      /* Reads API response */
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

      /* Close the connection. */
      connection.disconnect();
    } catch (IOException ioe) {
      if (debugModeValue) {
        logger.severe(ioe.getMessage());
      }
    }
  }

  public String getUrl() {
    return urlApi;
  }

  public void setUrlApi(String urlApi) {
    this.urlApi = urlApi;
  }

  public void setDebugModeValue(boolean debugModeValue) {
    this.debugModeValue = debugModeValue;
  }
}
