package br.uefs.larsid.iot.soft.services;

/**
 *
 * @author Allan Capistrano
 */
public interface ILedgerReader {
  public void subscribe(String topic, ILedgerSubscriber subscriber);

  public void unsubscribe(String topic, ILedgerSubscriber subscriber);
}
