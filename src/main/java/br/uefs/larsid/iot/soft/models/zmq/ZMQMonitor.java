package br.uefs.larsid.iot.soft.models.zmq;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;
import br.uefs.larsid.iot.soft.models.transactions.Evaluation;
import br.uefs.larsid.iot.soft.models.transactions.Transaction;
import br.uefs.larsid.iot.soft.services.ILedgerSubscriber;
import br.uefs.larsid.iot.soft.utils.CsvWriter;
import java.util.logging.Logger;

/**
 *
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class ZMQMonitor implements ILedgerSubscriber {

  /*---------------------------- Constantes ----------------------------------*/
  private static String[] CSV_HEADER = { "Time (s)", "Responde Time (ms)" };
  /*--------------------------------------------------------------------------*/

  /*----------------------------- CSV ----------------------------------------*/
  private String[] csvData = new String[2];
  private int csvIndex;
  private final CsvWriter csvWriter;
  /*--------------------------------------------------------------------------*/

  private ZMQListener zmqListener;

  private static final Logger logger = Logger.getLogger(
    ZMQMonitor.class.getName()
  );

  public ZMQMonitor(
    String protocol,
    String url,
    String apiPort,
    int bufferSize,
    String zmqSocketProtocol,
    String zmqSocketUrl,
    String zmqSocketPort,
    CsvWriter csvWriter
  ) {
    new ZMQPublisher(
      protocol,
      url,
      Integer.parseInt(apiPort),
      bufferSize,
      false
    );

    this.zmqListener =
      new ZMQListener(
        bufferSize,
        zmqSocketProtocol,
        zmqSocketUrl,
        zmqSocketPort,
        false
      );

    this.zmqListener.subscribe(
        TransactionType.REP_ZMQ_MONITOR.toString(),
        this
      );

    this.csvWriter = csvWriter;
    this.csvIndex = 0;

    this.csvWriter.writeData(CSV_HEADER);
  }

  @Override
  public void update(Object object, Object object2) {
    if (((Transaction) object).getType() == TransactionType.REP_ZMQ_MONITOR) {
      Evaluation receivedTransaction = (Evaluation) object;

      long start = receivedTransaction.getPublishedAt();
      long end = System.currentTimeMillis();
      long responseTime = end - start;

      logger.info("ZMQ response time (ms): " + responseTime + "\n");

      if (this.csvWriter != null) {
        this.csvData[0] =
          String.valueOf(ZMQPublisher.SLEEP / 1000 * this.csvIndex);
        this.csvData[1] = String.valueOf(responseTime);

        this.csvWriter.writeData(this.csvData);

        this.csvIndex++;
      }
    }
  }
}
