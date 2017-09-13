package org.asgs.twitterfeeds;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Analyzer WebappApplication extends Application<Analyzer WebappConfiguration> {

    public static void main(final String[] args) throws Exception {
        new Analyzer WebappApplication().run(args);
    }

    @Override
    public String getName() {
        return "Analyzer Webapp";
    }

    @Override
    public void initialize(final Bootstrap<Analyzer WebappConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final Analyzer WebappConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
