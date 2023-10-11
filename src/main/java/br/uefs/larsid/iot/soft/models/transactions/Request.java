package br.uefs.larsid.iot.soft.models.transactions;

import br.uefs.larsid.iot.soft.models.enums.TransactionType;

/**
 *
 * @author Uellington Damasceno
 */
public class Request extends TargetedTransaction {

  private final String device;

  public Request(String source, String group, String device, String target) {
    super(source, group, TransactionType.LB_REQUEST, target);
    this.device = device;
  }

  public String getDevice() {
    return this.device;
  }
}
