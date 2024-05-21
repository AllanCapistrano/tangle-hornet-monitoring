package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;
import br.uefs.larsid.iot.soft.models.transactions.Evaluation;
import br.uefs.larsid.iot.soft.models.transactions.Transaction;
import br.uefs.larsid.iot.soft.services.ILedgerSubscriber;
import java.util.logging.Logger;

/**
 *
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class ZMQMonitor implements ILedgerSubscriber {

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
    String zmqSocketPort
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
  }

  @Override
  public void update(Object object, Object object2) {
    if (((Transaction) object).getType() == TransactionType.REP_ZMQ_MONITOR) {
      Evaluation receivedTransaction = (Evaluation) object;

      logger.info(String.valueOf(receivedTransaction.getPublishedAt()));
    }
  }
}
