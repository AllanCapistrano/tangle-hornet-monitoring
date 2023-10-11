package br.uefs.larsid.iot.soft;

import br.uefs.larsid.iot.soft.models.LedgerReader;
import br.uefs.larsid.iot.soft.utils.CLI;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class Main {

  /*----------------------------- Constants ----------------------------------*/
  private static String PROTOCOL = "http";
  private static String URL = "127.0.0.1";
  /*--------------------------------------------------------------------------*/

  /*----------------------------- Properties ---------------------------------*/
  private static String apiPort;
  private static String tag;
  /*--------------------------------------------------------------------------*/

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    logger.info("Starting Tangle Reader...");

    readProperties(args);

    new LedgerReader(PROTOCOL, URL, Integer.parseInt(apiPort), tag, false);
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

      tag = CLI.getTag(args).orElse(props.getProperty("tag"));
    } catch (IOException ex) {
      logger.warning("Sorry, unable to find tangle-monitor.properties.");
    }
  }
}
