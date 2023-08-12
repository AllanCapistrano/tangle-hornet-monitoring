package br.uefs.larsid.iot.soft.utils;

import java.util.Arrays;
import java.util.List;
/**
 *
 * @author Allan Capistrano
 */
import java.util.Optional;

public class CLI {

  public static Optional<String> getProtocol(String... args) {
    return getArgInList("-ptc", args);
  }

  public static Optional<String> getURL(String... args) {
    return getArgInList("-url", args);
  }

  public static Optional<String> getPort(String... args) {
    return getArgInList("-prt", args);
  }

  public static Optional<String> getTag(String... args) {
    return getArgInList("-tag", args);
  }

  public static boolean hasParam(String arg, String... args) {
    return Arrays.asList(args).indexOf(arg) != -1;
  }

  private static Optional<String> getArgInList(String arg, String... args) {
    List<String> largs = Arrays.asList(args);
    int index = largs.indexOf(arg);

    return (Optional<String>) (
      (index == -1) ? Optional.empty() : Optional.of(largs.get(index + 1))
    );
  }
}
