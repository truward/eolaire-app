package com.truward.orion.eolaire.server;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point.
 *
 * @author Alexander Shabanov
 */
public final class EolaireLauncher extends StandardLauncher {
  private final ResourceHandler resourceHandler;

  public EolaireLauncher(@Nullable String overrideStaticPath) throws IOException {
    this.resourceHandler = createStaticHandler(overrideStaticPath);
  }

  public static void main(String[] args) throws Exception {
    final List<String> argList = Arrays.asList(args);
    final int overrideStaticPathIndex = argList.indexOf("--dev-override-static-path") + 1;

    new EolaireLauncher(overrideStaticPathIndex > 0 ? argList.get(overrideStaticPathIndex) : null)
        .setDefaultDirPrefix("classpath:/eolaireService/")
        .setSimpleSecurityEnabled(!argList.contains("--dev-disable-simple-security"))
        .setAuthPropertiesPrefix("eolaireService.auth")
        .start();
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
  private ResourceHandler createStaticHandler(@Nullable String overrideStaticPath) throws IOException {
    final ResourceHandler resourceHandler = new ResourceHandler();
    if (overrideStaticPath != null) {
      getLogger().info("Using override path for static resources: {}", overrideStaticPath);
      resourceHandler.setBaseResource(Resource.newResource(new File(overrideStaticPath)));
    } else {
      resourceHandler.setBaseResource(Resource.newClassPathResource("/eolaireService/web"));
    }
    return resourceHandler;
  }
}
