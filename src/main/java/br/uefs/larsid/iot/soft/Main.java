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

  /*---------------------------------Properties-------------------------------*/
  private static String protocol;
  private static String url;
  private static String port;
  private static String tag;
  /*--------------------------------------------------------------------------*/

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    logger.info("Starting Tangle Reader...");

    readProperties(args);

    new LedgerReader(protocol, url, Integer.parseInt(port), tag, false);
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

      protocol = CLI.getProtocol(args).orElse(props.getProperty("protocol"));

      url = CLI.getURL(args).orElse(props.getProperty("url"));

      port = CLI.getPort(args).orElse(props.getProperty("port"));

      tag = CLI.getTag(args).orElse(props.getProperty("tag"));
    } catch (IOException ex) {
      logger.warning("Sorry, unable to find tangle-monitor.properties.");
    }
  }
}
