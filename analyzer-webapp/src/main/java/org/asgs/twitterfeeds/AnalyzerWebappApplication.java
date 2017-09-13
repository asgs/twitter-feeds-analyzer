package org.asgs.twitterfeeds;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.asgs.twitterfeeds.resources.StatsResource;

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
        // TODO: application initialization
    }

    @Override
    public void run(final AnalyzerWebappConfiguration configuration,
                    final Environment environment) {
        StatsResource resource = new StatsResource();
        environment.jersey().register(resource);
    }

}
