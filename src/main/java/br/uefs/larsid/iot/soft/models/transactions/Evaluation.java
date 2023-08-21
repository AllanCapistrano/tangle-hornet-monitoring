package br.uefs.larsid.iot.soft.models.transactions;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;

public class Evaluation extends Transaction {

  private final int value;

  public Evaluation(
    String source,
    String target,
    TransactionType type,
    int value
  ) {
    super(source, target, type);
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public String getTarget() {
    return this.getGroup();
  }

  @Override
  public String toString() { // TODO: melhorar exibição
    return new StringBuilder("Transaction: ")
      .append(this.getValue())
      .append(this.getSource())
      .append(this.getGroup())
      .append(this.getType())
      .append(this.getCreatedAt())
      .append(this.getPublishedAt())
      .toString();
  }
}
