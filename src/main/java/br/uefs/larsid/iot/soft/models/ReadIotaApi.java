package br.uefs.larsid.iot.soft.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.iota.jota.IotaAPI;
import org.iota.jota.dto.response.FindTransactionResponse;
import org.iota.jota.dto.response.GetBundleResponse;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.utils.TrytesConverter;

/**
 *
 * @author Allan Capistrano
 */
public class ReadIotaApi implements Runnable {

  /*-------------------------Constantes---------------------------------------*/
  private static final long SLEEP = 5000;
  /*--------------------------------------------------------------------------*/

  private Thread readApi;
  private final IotaAPI api;
  private final String tag;
  private static final Logger logger = Logger.getLogger(
    ReadIotaApi.class.getName()
  );

  /**
   * Método construtor.
   *
   * @param protocol - Protocolo DLT
   * @param url - URL DLT
   * @param port - Porta DLT
   */
  public ReadIotaApi(String protocol, String url, int port, String tag) {
    this.api =
      new IotaAPI.Builder().protocol(protocol).host(url).port(port).build();
    this.tag = tag;

    if (this.readApi == null) {
      this.readApi = new Thread(this);
      this.readApi.setName("TANGLE_MONITOR/READ_API");
      this.readApi.start();
    }
  }

  /**
   * Obtém todas as transações presentes na rede a partir de uma TAG.
   *
   * @param tag String - TAG que se deseja consultar
   * @return List<String>
   * @throws ArgumentException
   */
  public List<String> findTransactionsByTag(String tag)
    throws ArgumentException {
    List<String> transactionsInJson = new ArrayList<>();

    for (String hashTransaction : this.getHashesByTag(tag)) {
      logger.info(hashTransaction); // TODO: Remover

      String transaction = this.getTransactionByHash(hashTransaction);

      transactionsInJson.add(transaction);
    }

    return transactionsInJson;
  }

  /**
   * Obtém a primeira transação presente na rede de uma determinada TAG.
   *
   * @param tag String - TAG que se deseja consultar
   * @return String
   * @throws ArgumentException
   */
  public String findFirstTransactionByTag(String tag) {
    String[] hashesTransaction = this.getHashesByTag(tag);

    return this.getTransactionByHash(hashesTransaction[0]);
  }

  /**
   * Obtém os hashes de todas as transações de uma determinada TAG.
   *
   * @param tag String - TAG que se deseja consultar
   * @return String[]
   * @throws ArgumentException
   */
  private String[] getHashesByTag(String tag) throws ArgumentException {
    String tagTrytes = TrytesConverter.asciiToTrytes(tag);

    FindTransactionResponse transactions =
      this.api.findTransactionsByTags(tagTrytes);

    return transactions.getHashes();
  }

  /**
   * Obtém uma transação a partir do hash.
   *
   * @param hashTransaction String - Hash da transação.
   * @return String
   * @throws ArgumentException
   */
  private String getTransactionByHash(String hashTransaction)
    throws ArgumentException {
    GetBundleResponse response = api.getBundle(hashTransaction);

    String transactionTrytes = response
      .getTransactions()
      .get(0)
      .getSignatureFragments()
      .substring(0, 2186);

    return TrytesConverter.trytesToAscii(transactionTrytes);
  }

  /**
   * Thread para obter a(s) transação/transações.
   */
  @Override
  public void run() {
    while (!this.readApi.isInterrupted()) {
      try {
        long start = System.currentTimeMillis();

        this.findFirstTransactionByTag(this.tag);

        long end = System.currentTimeMillis();
        logger.info("API Response time (ms): " + (end - start));

        Thread.sleep(SLEEP);
      } catch (ArgumentException ae) {
        logger.warning(ae.getStackTrace().toString());
      } catch (InterruptedException ie) {
        logger.warning(ie.getStackTrace().toString());
      }
    }
  }
}
