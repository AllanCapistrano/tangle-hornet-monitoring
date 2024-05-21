package br.uefs.larsid.iot.soft.models;

import br.uefs.larsid.iot.soft.models.tangle.Message;
import br.uefs.larsid.iot.soft.models.transactions.Transaction;
import br.uefs.larsid.iot.soft.services.ILedgerSubscriber;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class ZMQListener implements Runnable {

  private ZMQServer server;
  private Thread DLTInboundMonitor;
  private boolean debugModeValue;
  private final Map<String, Set<ILedgerSubscriber>> topics;

  private static final Logger logger = Logger.getLogger(
    ZMQListener.class.getName()
  );

  public ZMQListener(
    int bufferSize,
    String socketProtocol,
    String socketUrl,
    String socketPort,
    boolean debugModeValue
  ) {
    this.topics = new HashMap<String, Set<ILedgerSubscriber>>();

    this.server =
      new ZMQServer(bufferSize, socketProtocol, socketUrl, socketPort);
    this.debugModeValue = debugModeValue;

    if (this.DLTInboundMonitor == null) {
      this.DLTInboundMonitor = new Thread(this);
      this.DLTInboundMonitor.setName("TANGLE_MONITOR/ZMQ_LISTENER");
      this.DLTInboundMonitor.start();
    }
  }

  public void subscribe(String topic, ILedgerSubscriber subscriber) {
    if (topic != null) {
      Set<ILedgerSubscriber> subscribers = this.topics.get(topic);
      if (subscribers != null) {
        subscribers.add(subscriber);
      } else {
        subscribers = new HashSet<ILedgerSubscriber>();
        subscribers.add(subscriber);
        this.topics.put(topic, subscribers);
        this.server.subscribe(topic);
      }
    }
  }

  public void unsubscribe(String topic, ILedgerSubscriber subscriber) {
    if (topic != null) {
      Set<ILedgerSubscriber> subscribers = this.topics.get(topic);
      if (subscribers != null && !subscribers.isEmpty()) {
        subscribers.remove(subscriber);
        if (subscribers.isEmpty()) {
          this.server.unsubscribe(topic);
          this.topics.remove(topic);
        }
      }
    }
  }

  private void notifyAll(String topic, Object object, Object object2) {
    if (topic != null && !topic.isEmpty()) {
      Set<ILedgerSubscriber> subscribers = this.topics.get(topic);
      if (subscribers != null && !subscribers.isEmpty()) {
        subscribers.forEach(sub -> sub.update(object, object2));
      }
    }
  }

  @Override
  public void run() {
    while (!this.DLTInboundMonitor.isInterrupted()) {
      try {
        String receivedMessage = this.server.take();

        if (receivedMessage != null && receivedMessage.contains("/")) {
          Gson gson = new Gson();
          String[] data = receivedMessage.split("/", 2);
          String topic = data[0];

          Message message = gson.fromJson(data[1], Message.class);

          notifyAll(
            topic,
            Transaction.getTransactionObjectByType(
              message.getPayload().getData(),
              debugModeValue
            ),
            message.getId()
          );
        }
      } catch (InterruptedException ex) {
        logger.info(ex.getMessage());
        this.DLTInboundMonitor.interrupt();
      }
    }
  }
}
