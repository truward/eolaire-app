package com.truward.orion.eolaire.server;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * Entry point.
 *
 * @author Alexander Shabanov
 */
public final class EolaireLauncher {

  public static void main(String[] args) throws Exception {
    try (StandardLauncher launcher = new StandardLauncher("classpath:/eolaireService/")) {
      launcher.setAuthPropertiesPrefix("eolaireService.auth");
      launcher
          .setSimpleSecurityEnabled(true)
          .setStaticHandlerEnabled(true)
          .start();
    }
  }
}
