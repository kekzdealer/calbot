package at.kurumi.routines;

import at.kurumi.docker.ContainerRunningEvent;
import at.kurumi.docker.DockerInterface;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.PostConstruct;

@ApplicationScoped
public class StartupRoutine {

    private final DockerInterface dockerInterface;

    @Inject
    public StartupRoutine(DockerInterface dockerInterface) {
        this.dockerInterface = dockerInterface;
    }

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        dockerInterface.newContainer("/at/kurumi/routines/kurumi-db.dc", true);
    }

    private void startDiscordInterface(@Observes ContainerRunningEvent condition) {

    }

}
