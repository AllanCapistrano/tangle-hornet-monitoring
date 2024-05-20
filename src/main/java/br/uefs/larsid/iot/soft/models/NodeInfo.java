package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.utils.CsvWriter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class NodeInfo implements Runnable {

  /*---------------------------- JSON Keys -----------------------------------*/
  private static final String MILESTONE = "milestone";
  private static final String MESSAGES = "messages";
  private static final String IS_HEALTHY = "is_healthy";
  private static final String VERSION = "version";
  private static final String CONFIRMED_MILESTONE_INDEX =
    "confirmed_milestone_index";
  private static final String LATEST_MILESTONE_INDEX = "latest_milestone_index";
  private static final String LATEST_MILESTONE_TIMESTAMP =
    "latest_milestone_timestamp";
  private static final String MESSAGES_PER_SECOND = "messages_per_second";
  private static final String REFERENCED_MESSAGES_PER_SECOND =
    "referenced_messages_per_second";
  private static final String REFERENCED_RATE = "referenced_rate";
  /*--------------------------------------------------------------------------*/

  /*---------------------------- Constantes ----------------------------------*/
  private static final long SLEEP = 15000;
  private static final String ENDPOINT = "nodeInfo/all";
  private static String[] CSV_HEADER = {
    "Time (s)",
    IS_HEALTHY,
    VERSION,
    CONFIRMED_MILESTONE_INDEX,
    LATEST_MILESTONE_INDEX,
    LATEST_MILESTONE_TIMESTAMP,
    MESSAGES_PER_SECOND,
    REFERENCED_MESSAGES_PER_SECOND,
    REFERENCED_RATE,
  };
  /*--------------------------------------------------------------------------*/

  /*----------------------------- CSV ----------------------------------------*/
  private String[] csvData = new String[9];
  private int csvIndex;
  private final CsvWriter csvWriter;
  /*--------------------------------------------------------------------------*/

  private Thread nodeInfo;
  private String urlApi;
  private boolean debugModeValue;

  private static final Logger logger = Logger.getLogger(
    NodeInfo.class.getName()
  );

  public NodeInfo(
    String protocol,
    String url,
    int port,
    CsvWriter csvWriter,
    boolean debugModeValue
  ) {
    this.urlApi = String.format("%s://%s:%s", protocol, url, port);
    this.debugModeValue = debugModeValue;
    this.csvWriter = csvWriter;
    this.csvIndex = 0;

    this.csvWriter.writeData(CSV_HEADER);

    if (this.nodeInfo == null) {
      this.nodeInfo = new Thread(this);
      this.nodeInfo.setName("TANGLE_MONITOR/NODE_INFO");
      this.nodeInfo.start();
    }
  }

  /**
   * Obtém as informações do nó da Tangle Hornet consultando o endpoint
   * '/nodeInfo/all'
   *
   * @return String
   */
  private String getNodeInfo() {
    String response = null;

    try {
      URL url = new URL(String.format("%s/%s", this.urlApi, ENDPOINT));
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new RuntimeException(
          "HTTP error code : " + conn.getResponseCode()
        );
      }

      BufferedReader br = new BufferedReader(
        new InputStreamReader((conn.getInputStream()))
      );

      String temp = null;

      while ((temp = br.readLine()) != null) {
        response = temp;
      }

      conn.disconnect();

      this.csvData[1] = String.valueOf(this.isNodeHealthy(response));
      this.csvData[2] = String.valueOf(this.getNodeVersion(response));
      this.csvData[3] =
        String.valueOf(this.getConfirmedMilestoneIndex(response));
      this.csvData[4] = String.valueOf(this.getLatestMilestoneIndex(response));
      this.csvData[5] =
        String.valueOf(this.getLatestMilestoneTimestamp(response));
      this.csvData[6] = String.valueOf(this.getMessagesPerSecond(response));
      this.csvData[7] =
        String.valueOf(this.getReferencedMessagesPerSecond(response));
      this.csvData[8] = String.valueOf(this.getReferencedRate(response));
    } catch (MalformedURLException mue) {
      if (debugModeValue) {
        logger.severe(mue.getMessage());
      }
    } catch (IOException ioe) {
      if (debugModeValue) {
        logger.severe(ioe.getMessage());
      }
    }

    return response;
  }

  /**
   * Obtém a informação se o nó está funcionando corretamente.
   *
   * @param json String - Resposta da API.
   * @return boolean
   */
  private boolean isNodeHealthy(String json) {
    return this.getJsonElement(json, IS_HEALTHY).getAsBoolean();
  }

  /**
   * Obtém a versão da Tangle Hornet
   *
   * @param json String - Resposta da API.
   * @return String
   */
  private String getNodeVersion(String json) {
    return this.getJsonElement(json, VERSION).getAsString();
  }

  /**
   * Obtém o índice da última milestones confirmada.
   *
   * @param json String - Resposta da API.
   * @return int
   */
  private int getConfirmedMilestoneIndex(String json) {
    return this.getJsonElement(json, MILESTONE)
      .getAsJsonObject()
      .get(CONFIRMED_MILESTONE_INDEX)
      .getAsInt();
  }

  /**
   * Obtém o índice da última milestone criada.
   *
   * @param json String - Resposta da API.
   * @return int
   */
  private int getLatestMilestoneIndex(String json) {
    return this.getJsonElement(json, MILESTONE)
      .getAsJsonObject()
      .get(LATEST_MILESTONE_INDEX)
      .getAsInt();
  }

  /**
   * Obtém a data e hora (em ms) da última milestone criada.
   
   * @param json String - Resposta da API.
   * @return long
   */
  private long getLatestMilestoneTimestamp(String json) {
    return this.getJsonElement(json, MILESTONE)
      .getAsJsonObject()
      .get(LATEST_MILESTONE_TIMESTAMP)
      .getAsLong();
  }

  /**
   * Obtém a quantidade de mensagens por segundo da rede.
   *
   * @param json String - Resposta da API.
   * @return double
   */
  private double getMessagesPerSecond(String json) {
    return this.getJsonElement(json, MESSAGES)
      .getAsJsonObject()
      .get(MESSAGES_PER_SECOND)
      .getAsDouble();
  }

  /**
   * Obtém a quantidade de mensagens referenciadas por segundo da rede.
   *
   * @param json String - Resposta da API.
   * @return double
   */
  private double getReferencedMessagesPerSecond(String json) {
    return this.getJsonElement(json, MESSAGES)
      .getAsJsonObject()
      .get(REFERENCED_MESSAGES_PER_SECOND)
      .getAsDouble();
  }

  /**
   * Obtém a taxa de referências das mensagens da rede.
   *
   * @param json String - Resposta da API.
   * @return double
   */
  private double getReferencedRate(String json) {
    return this.getJsonElement(json, MESSAGES)
      .getAsJsonObject()
      .get(REFERENCED_RATE)
      .getAsDouble();
  }

  /**
   * Pega o elemento do JSON a partir da chave informada.
   *
   * @param json String - JSON no formato String.
   * @param key String - Chave do elemento.
   * @return JsonElement
   */
  private JsonElement getJsonElement(String json, String key) {
    JsonReader reader = new JsonReader(new StringReader(json));

    reader.setLenient(true);

    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

    return jsonObject.get(key);
  }

  @Override
  public void run() {
    while (!this.nodeInfo.isInterrupted()) {
      try {
        long start = System.currentTimeMillis();

        logger.info(this.getNodeInfo());

        long end = System.currentTimeMillis();
        long responseTime = end - start;

        logger.info("Node info response time (ms): " + responseTime + "\n");

        if (this.csvWriter != null) {
          this.csvData[0] = String.valueOf(SLEEP / 1000 * this.csvIndex);

          this.csvWriter.writeData(this.csvData);

          this.csvIndex++;
        }

        Thread.sleep(SLEEP);
      } catch (InterruptedException ie) {
        logger.warning(ie.getStackTrace().toString());
      }
    }
  }
}
