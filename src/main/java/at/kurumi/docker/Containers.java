package at.kurumi.docker;

import at.kurumi.docker.director.ContainerParameter;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Containers {

    public List<Container> list(DockerClient client, boolean showSize, boolean showAll, Collection<String> statusFilter) {
        return client.listContainersCmd()
                .withShowSize(showSize)
                .withShowAll(showAll)
                .withStatusFilter(statusFilter)
                .exec();
    }

    public void start(DockerClient client, Container container) {
        client.startContainerCmd(container.getId()).exec();
    }

    public void stop(DockerClient client, Container container) {
        client.stopContainerCmd(container.getId()).exec();
    }

    public void kill(DockerClient client, Container container) {
        client.killContainerCmd(container.getId()).exec();
    }

    public InspectContainerResponse inspect(DockerClient client, Container container) {
        return client.inspectContainerCmd(container.getId()).exec();
    }

    public void healthcheck(DockerClient client, Container container) {
        container.getState()
    }
}
