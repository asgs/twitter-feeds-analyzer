package org.asgs.twitterfeeds;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.asgs.twitterfeeds.resources.StatsResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class AnalyzerWebappApplication extends Application<AnalyzerWebappConfiguration> {

  public static void main(final String[] args) throws Exception {
    new AnalyzerWebappApplication().run(args);
  }

  @Override
  public String getName() {
    return "Analyzer Webapp";
  }

  @Override
  public void initialize(final Bootstrap<AnalyzerWebappConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/templates", "/stats", "stats.html", "static"));
  }

  @Override
  public void run(final AnalyzerWebappConfiguration configuration, final Environment environment) {
    StatsResource resource = new StatsResource();
    environment.jersey().register(resource);
    environment.jersey().register(SseFeature.class);
    final FilterRegistration.Dynamic cors =
        environment.servlets().addFilter("CORS", CrossOriginFilter.class);

    // Configure CORS parameters
    cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
    cors.setInitParameter(
        CrossOriginFilter.ALLOWED_HEADERS_PARAM,
        "X-Requested-With,Content-Type,Accept,Origin,Authorization");
    cors.setInitParameter(
        CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

    // Add URL mapping
    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
  }
}
