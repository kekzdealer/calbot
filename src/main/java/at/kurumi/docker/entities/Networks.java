package at.kurumi.docker.entities;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;

public class Networks {

    public CreateNetworkResponse create(DockerClient client, String name, String driver) {
        return client.createNetworkCmd()
                .withName(name)
                .withDriver(driver)
                .exec();
    }

    public Network inspect(DockerClient client, String name) {
        return client.inspectNetworkCmd().withNetworkId(name).exec();
    }

    public void remove(DockerClient client, String name) {
        client.removeNetworkCmd(name).exec();
    }
}
