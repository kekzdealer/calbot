package at.kurumi.routines;

import at.kurumi.docker.ContainerAvailableEvent;
import at.kurumi.docker.DockerInterface;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class StartupRoutine {

    private final DockerInterface dockerInterface;

    @Inject
    public StartupRoutine(DockerInterface dockerInterface) {
        this.dockerInterface = dockerInterface;
    }

    @PostConstruct
    public void onStart() {
        dockerInterface.newContainer("/at/kurumi/routines/kurumi-db.dc", true);
    }

    private void startDiscordInterface(@Observes ContainerAvailableEvent condition) {

    }

}
