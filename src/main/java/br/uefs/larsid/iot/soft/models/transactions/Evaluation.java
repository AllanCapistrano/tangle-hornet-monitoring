package br.uefs.larsid.iot.soft.models.transactions;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;

public class Evaluation extends Transaction {

  private final int value;

  public Evaluation(
    String source,
    String target,
    TransactionType type,
    int value,
    long publishedAt
  ) {
    super(source, target, type);
    this.setPublishedAt(publishedAt);
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public String getTarget() {
    return this.getGroup();
  }

  @Override
  public String toString() {
    return String.format(
      "Transaction: value: %s, source: %s, target: %s, type: %s, createdAt: %d, publishedAt: %d",
      this.getValue(),
      this.getSource(),
      this.getTarget(),
      this.getType(),
      this.getCreatedAt(),
      this.getPublishedAt()
    );
  }
}
