package at.kurumi.docker;

/**
 * Event signalling that a Docker container has been started and passed its first health-check
 */
public class ContainerAvailableEvent {

    private String containerName;

    public ContainerAvailableEvent(String containerName) {
        this.containerName = containerName;
    }
}
