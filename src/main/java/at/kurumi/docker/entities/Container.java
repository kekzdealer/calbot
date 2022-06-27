package at.kurumi.docker.entities;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Volume;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a docker container.
 */
public class Container implements Comparable<Container> {

    /**
     * Parameters to pass to the {@code docker create} command.
     */
    private ContainerParameter parameter;

    /**
     * Containers that have to be running before this one can start.
     */
    private Set<String> dependsOn;

    public void setParameter(ContainerParameter parameter) {
        this.parameter = parameter;
    }

    public void addDependency(String dependsOn) {
        if(this.dependsOn == null) {
            this.dependsOn = new HashSet<>();
        }
        this.dependsOn.add(dependsOn);
    }

    public CreateContainerResponse create(DockerClient dockerClient) {
        if(parameter == null) {
            return null;
        }
        final var createContainerCmd = dockerClient.createContainerCmd(parameter.getImage())
                .withName(parameter.getContainerName());

        final var volumes = parameter.getVolumes().stream()
                .map(Volume::parse)
                .collect(Collectors.toList());
        createContainerCmd.withVolumes(volumes);

        createContainerCmd.withEnv(parameter.getEnvironment());

        createContainerCmd.withPortSpecs(parameter.getPorts());

        return createContainerCmd.exec();
    }

    public void start(DockerClient client) {
        client.startContainerCmd(parameter.getContainerName()).exec();
    }

    public void stop(DockerClient client) {
        client.stopContainerCmd(parameter.getContainerName()).exec();
    }

    public void kill(DockerClient client) {
        client.killContainerCmd(parameter.getContainerName()).exec();
    }

    public void isHealthy(DockerClient client) {
        final var icr = client.inspectContainerCmd(parameter.getContainerName()).exec();
        final var status = icr.getState().getHealth().getStatus();
        System.out.println(status);
        // TODO parse status and return boolean
    }

    public String getName() {
        return parameter.getContainerName();
    }

    @Override
    public int compareTo(Container o) {
        return this.dependsOn.contains(o.parameter.getContainerName()) ? 1 : 0;
    }

}
