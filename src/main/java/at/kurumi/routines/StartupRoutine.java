package at.kurumi.routines;

import at.kurumi.docker.DockerInterface;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class StartupRoutine {

    @Inject private DockerInterface dockerInterface;

    @PostConstruct
    public void onStart() {
        System.out.println("ALLLLLLLLLLLLLLLOOOOOOOOOOOOOOOOOOO");
        final var rootLogger = Logger.getLogger("");
        final var logLevel = Level.ALL;
        rootLogger.setLevel(logLevel);
        for(var handler : rootLogger.getHandlers()) {
            handler.setLevel(logLevel);
        }
        rootLogger.severe("Set log level to: " + logLevel.getName());

        dockerInterface.newContainer("/at/kurumi/routines/kurumi-db.dc", true);
    }

}
