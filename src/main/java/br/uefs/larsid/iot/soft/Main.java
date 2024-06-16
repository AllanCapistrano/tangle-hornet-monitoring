package br.uefs.larsid.iot.soft;

import br.uefs.larsid.iot.soft.models.LedgerReader;
import br.uefs.larsid.iot.soft.models.LedgerWriter;
import br.uefs.larsid.iot.soft.models.NodeInfo;
import br.uefs.larsid.iot.soft.models.zmq.ZMQMonitor;
import br.uefs.larsid.iot.soft.utils.CLI;
import br.uefs.larsid.iot.soft.utils.CsvWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Allan Capistrano
 * @version 1.3.0
 */
public class Main {

  /*---------------------------- Constantes ----------------------------------*/
  private static String PROTOCOL = "http";
  private static String URL = "127.0.0.1";
  private static int BUFFER_SIZE = 128;
  /*--------------------------------------------------------------------------*/

  /*----------------------------- Propriedades -------------------------------*/
  private static String apiPort;
  private static String readIndex;
  private static String writeIndex;
  private static String zmqSocketProtocol;
  private static String zmqSocketUrl;
  private static String zmqSocketPort;
  private static String readMultipleIndex;
  private static boolean isMonitoringWriting = false;
  private static boolean isMonitoringReading = false;
  private static boolean isMonitoringNode = false;
  private static boolean isMonitoringZMQ = false;
  private static boolean isMonitoringReadingMultipleTransactions = false;
  /*--------------------------------------------------------------------------*/

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    readProperties(args);

    if (
      !isMonitoringReading &&
      !isMonitoringWriting &&
      !isMonitoringNode &&
      !isMonitoringZMQ &&
      !isMonitoringReadingMultipleTransactions
    ) {
      isMonitoringReading = true;
    }

    if (isMonitoringReading) {
      logger.info("Starting Tangle Reader...\n");

      CsvWriter csvWriter = new CsvWriter("tangle-reader");

      new LedgerReader(
        PROTOCOL,
        URL,
        Integer.parseInt(apiPort),
        readIndex,
        csvWriter,
        false,
        false
      );
    }

    if (isMonitoringReadingMultipleTransactions) {
      logger.info("Starting Tangle Reader...\n");

      CsvWriter csvWriter = new CsvWriter("tangle-reader");

      new LedgerReader(
        PROTOCOL,
        URL,
        Integer.parseInt(apiPort),
        readMultipleIndex,
        csvWriter,
        true,
        false
      );
    }

    if (isMonitoringWriting) {
      logger.info("Starting Tangle Writer...\n");

      CsvWriter csvWriter = new CsvWriter("tangle-writer");

      new LedgerWriter(
        PROTOCOL,
        URL,
        Integer.parseInt(apiPort),
        BUFFER_SIZE,
        writeIndex,
        csvWriter,
        false
      );
    }

    if (isMonitoringNode) {
      logger.info("Starting Node Info...\n");

      CsvWriter csvWriter = new CsvWriter("tangle-node-info");

      new NodeInfo(PROTOCOL, URL, Integer.parseInt(apiPort), csvWriter, false);
    }

    if (isMonitoringZMQ) {
      logger.info("Starting ZMQ Monitoring...\n");

      CsvWriter csvWriter = new CsvWriter("tangle-zmq");

      new ZMQMonitor(
        PROTOCOL,
        URL,
        apiPort,
        BUFFER_SIZE,
        zmqSocketProtocol,
        zmqSocketUrl,
        zmqSocketPort,
        csvWriter
      );
    }
  }

  /**
   * Realiza leitura das propriedades passadas por parâmetro ou resgata
   * valores presentes no arquivo de propriedade.
   *
   * @param args String[] - Dados passados na execução do projeto.
   */
  private static void readProperties(String[] args) {
    try (
      InputStream input = Main.class.getResourceAsStream(
          "tangle-hornet-monitoring.properties"
        )
    ) {
      if (input == null) {
        logger.warning(
          "Sorry, unable to find tangle-hornet-monitoring.properties."
        );
        return;
      }

      Properties props = new Properties();
      props.load(input);

      apiPort = CLI.getApiPort(args).orElse(props.getProperty("apiPort"));
      readIndex = CLI.getReadIndex(args).orElse(props.getProperty("readIndex"));
      writeIndex =
        CLI.getWriteIndex(args).orElse(props.getProperty("writeIndex"));
      readMultipleIndex =
        CLI.getReadMultipleIndex(args).orElse(props.getProperty("readMultipleIndex"));

      zmqSocketProtocol =
        CLI
          .getZMQSocketProtocol(args)
          .orElse(props.getProperty("zmqSocketProtocol"));
      zmqSocketUrl =
        CLI.getZMQSocketUrl(args).orElse(props.getProperty("zmqSocketUrl"));
      zmqSocketPort =
        CLI.getZMQSocketPort(args).orElse(props.getProperty("zmqSocketPort"));

      if (CLI.hasParam("-r", args)) {
        isMonitoringReading = true;
        isMonitoringWriting = false;
        isMonitoringNode = false;
        isMonitoringZMQ = false;
        isMonitoringReadingMultipleTransactions = false;
      }

      if (CLI.hasParam("-w", args)) {
        isMonitoringReading = false;
        isMonitoringWriting = true;
        isMonitoringNode = false;
        isMonitoringZMQ = false;
        isMonitoringReadingMultipleTransactions = false;
      }

      if (CLI.hasParam("-ni", args)) {
        isMonitoringReading = false;
        isMonitoringWriting = false;
        isMonitoringNode = true;
        isMonitoringZMQ = false;
        isMonitoringReadingMultipleTransactions = false;
      }

      if (CLI.hasParam("-z", args)) {
        isMonitoringReading = false;
        isMonitoringWriting = false;
        isMonitoringNode = false;
        isMonitoringZMQ = true;
        isMonitoringReadingMultipleTransactions = false;
      }

      if (CLI.hasParam("-rmi", args)) {
        isMonitoringReading = false;
        isMonitoringWriting = false;
        isMonitoringNode = false;
        isMonitoringZMQ = false;
        isMonitoringReadingMultipleTransactions = true;
      }
    } catch (IOException ex) {
      logger.warning("Sorry, unable to find tangle-monitor.properties.");
    }
  }
}
