package at.kurumi.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;

public class Volumes {

    public ListVolumesResponse list(DockerClient client) {
        return client.listVolumesCmd().exec();
    }

    public InspectVolumeResponse inspect(DockerClient client) {
        return client.inspectVolumeCmd("volume-short-id").exec();
    }

    public CreateVolumeResponse create(DockerClient client, String name) {
        return client.createVolumeCmd().withName(name).exec();
    }

    public void remove(DockerClient client, String name) {
        client.removeVolumeCmd(name).exec();
    }
}
