package br.uefs.larsid.iot.soft.models.transactions.reputation;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;
import br.uefs.larsid.iot.soft.models.transactions.TargetedTransaction;

/**
 *
 * @author Allan Capistrano
 */
public class Evaluation extends TargetedTransaction {

  private final float nodeCredibility;
  private final int serviceEvaluation;
  private final float value;

  public Evaluation(
    String source,
    String target,
    String group,
    TransactionType type,
    int serviceEvaluation,
    float nodeCredibility,
    float value
  ) {
    super(source, group, type, target);
    this.serviceEvaluation = serviceEvaluation;
    this.nodeCredibility = nodeCredibility;
    this.value = value;
  }

  public int getServiceEvaluation() {
    return serviceEvaluation;
  }

  public float getNodeCredibility() {
    return nodeCredibility;
  }

  public float getValue() {
    return value;
  }
}
