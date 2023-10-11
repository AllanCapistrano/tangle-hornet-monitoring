package br.uefs.larsid.iot.soft.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author Allan Capistrano
 */
import java.util.Optional;

public class CLI {

  public static Optional<String> getApiPort(String... args) {
    return getArgInList("-apt", args);
  }

  public static Optional<String> getTag(String... args) {
    return getArgInList("-tag", args);
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
