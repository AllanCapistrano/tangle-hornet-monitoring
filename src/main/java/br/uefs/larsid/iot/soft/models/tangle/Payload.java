package br.uefs.larsid.iot.soft.models.tangle;

import com.google.gson.Gson;

/**
 * @author Allan Capistrano
 */
public class Payload {
  private String data;
  private String index;

  /**
   * Converts a string into a Payload object.
   * 
   * @param string String - String that will be converted.
   * @return Payload
   */
  public static Payload stringToPayload(String string) {
    return new Gson().fromJson(string, Payload.class);
  }

  public String getData() {
    return data;
  }

  public String getIndex() {
    return index;
  }
}
