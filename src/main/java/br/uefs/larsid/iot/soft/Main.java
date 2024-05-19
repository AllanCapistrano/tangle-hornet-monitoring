package br.uefs.larsid.iot.soft;

import br.uefs.larsid.iot.soft.models.LedgerReader;
import br.uefs.larsid.iot.soft.models.LedgerWriter;
import br.uefs.larsid.iot.soft.utils.CLI;
import br.uefs.larsid.iot.soft.utils.CsvWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Allan Capistrano
 * @version 1.1.0
 */
public class Main {

  /*---------------------------- Constantes ----------------------------------*/
  private static String PROTOCOL = "http";
  private static String URL = "127.0.0.1";
  /*--------------------------------------------------------------------------*/

  /*----------------------------- Propriedades -------------------------------*/
  private static String apiPort;
  private static String readIndex;
  private static String writeIndex;
  private static boolean isMonitoringWriting = false;
  private static boolean isMonitoringReading = false;
  /*--------------------------------------------------------------------------*/

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    readProperties(args);

    if (!isMonitoringReading && !isMonitoringWriting) {
      isMonitoringReading = true;
    }

    if (isMonitoringReading) {
      logger.info("Starting Tangle Reader...");

      CsvWriter csvWriter = new CsvWriter("tangle-reader");

      new LedgerReader(
        PROTOCOL,
        URL,
        Integer.parseInt(apiPort),
        readIndex,
        csvWriter,
        false
      );
    }

    if (isMonitoringWriting) {
      logger.info("Starting Tangle Writer...");

      CsvWriter csvWriter = new CsvWriter("tangle-writer");

      new LedgerWriter(
        PROTOCOL,
        URL,
        Integer.parseInt(apiPort),
        128,
        writeIndex,
        csvWriter,
        false
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
          "tangle-reader.properties"
        )
    ) {
      if (input == null) {
        logger.warning("Sorry, unable to find tangle-reader.properties.");
        return;
      }

      Properties props = new Properties();
      props.load(input);

      apiPort = CLI.getApiPort(args).orElse(props.getProperty("apiPort"));
      readIndex = CLI.getReadIndex(args).orElse(props.getProperty("readIndex"));
      writeIndex =
        CLI.getWriteIndex(args).orElse(props.getProperty("writeIndex"));

      if (CLI.hasParam("-r", args)) {
        isMonitoringReading = true;
        isMonitoringWriting = false;
      }

      if (CLI.hasParam("-w", args)) {
        isMonitoringWriting = true;
        isMonitoringReading = false;
      }
    } catch (IOException ex) {
      logger.warning("Sorry, unable to find tangle-monitor.properties.");
    }
  }
}
