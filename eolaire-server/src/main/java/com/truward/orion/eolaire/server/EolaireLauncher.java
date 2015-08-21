package com.truward.orion.eolaire.server;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point.
 *
 * @author Alexander Shabanov
 */
public final class EolaireLauncher extends StandardLauncher {
  private final ResourceHandler resourceHandler;

  public EolaireLauncher() throws Exception {
    super("classpath:/eolaireService/");
    this.resourceHandler = createStaticHandler();

    setAuthPropertiesPrefix("userService.auth");
    setSimpleSecurityEnabled(!getPropertyResolver().getProperty("brikar.dev.disableSecurity", Boolean.class, true));
  }

  public static void main(String[] args) throws Exception {
    try (final EolaireLauncher launcher = new EolaireLauncher()) {
      launcher.start();
    }
  }

  @Nonnull
  @Override
  protected List<Handler> getHandlers() {
    final List<Handler> handlers = new ArrayList<>(super.getHandlers());
    handlers.add(resourceHandler);
    return handlers;
  }

  //
  // Private
  //

  @Nonnull
  private ResourceHandler createStaticHandler() throws IOException {
    final ResourceHandler resourceHandler = new ResourceHandler();

    final String overrideStaticPath = getPropertyResolver().getProperty("brikar.dev.overrideStaticPath");
    if (overrideStaticPath != null) {
      getLogger().info("Using override path for static resources: {}", overrideStaticPath);
      resourceHandler.setBaseResource(Resource.newResource(new File(overrideStaticPath)));
    } else {
      resourceHandler.setBaseResource(Resource.newClassPathResource("/eolaireService/web"));
    }

    return resourceHandler;
  }
}
