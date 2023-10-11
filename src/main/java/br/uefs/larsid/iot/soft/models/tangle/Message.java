package br.uefs.larsid.iot.soft.models.tangle;

import java.util.List;

/**
 * @author Allan Capistrano
 */
public class Message {
  private String id;
  private long networkId;
  private int nonce;
  private List<String> parentMessageIds;
  private Payload payload;

  public String getId() {
    return id;
  }

  public long getNetworkId() {
    return networkId;
  }

  public int getNonce() {
    return nonce;
  }

  public List<String> getParentMessageIds() {
    return parentMessageIds;
  }

  public Payload getPayload() {
    return payload;
  }
}
