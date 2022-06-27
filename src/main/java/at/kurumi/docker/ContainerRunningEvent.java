package at.kurumi.docker;

/**
 * Event signalling that a Docker container has been started and passed its first health-check
 */
public class ContainerRunningEvent {

    private String containerName;

    public ContainerRunningEvent(String containerName) {
        this.containerName = containerName;
    }
}
