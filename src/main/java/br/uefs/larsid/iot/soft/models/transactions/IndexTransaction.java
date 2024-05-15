package br.uefs.larsid.iot.soft.models.transactions;

/**
 *
 * @author Allan Capistrano
 */
public class IndexTransaction {

  private final String index;
  private final Transaction transaction;

  public IndexTransaction(String index, Transaction transaction) {
    this.index = index;
    this.transaction = transaction;
  }

  public String getIndex() {
    return index;
  }

  public Transaction getTransaction() {
    return transaction;
  }
}
