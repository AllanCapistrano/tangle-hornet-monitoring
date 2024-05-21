package br.uefs.larsid.iot.soft.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Allan Capistrano
 */
public class CLI {

  public static Optional<String> getApiPort(String... args) {
    return getArgInList("-apt", args);
  }

  public static Optional<String> getReadIndex(String... args) {
    return getArgInList("-ridx", args);
  }

  public static Optional<String> getWriteIndex(String... args) {
    return getArgInList("-widx", args);
  }

  public static Optional<String> getZMQSocketProtocol(String... args) {
    return getArgInList("-zsp", args);
  }

  public static Optional<String> getZMQSocketUrl(String... args) {
    return getArgInList("-zsu", args);
  }
  
  public static Optional<String> getZMQSocketPort(String... args) {
    return getArgInList("-zspt", args);
  }

  public static boolean hasParam(String arg, String... args) {
    return Arrays.asList(args).indexOf(arg) != -1;
  }

  private static Optional<String> getArgInList(String arg, String... args) {
    List<String> largs = new ArrayList<>(Arrays.asList(args));
    int index = largs.indexOf(arg);

    return index != -1 && index < largs.size() - 1
      ? Optional.of(largs.get(index + 1))
      : Optional.empty();
  }
}
