package at.kurumi.docker.director;

import java.util.*;

public class ContainerParameter {

    /**
     * Name of the Docker image
     */
    private String image;

    /**
     * Name of the container after creation
     */
    private String containerName;

    /**
     * Port mappings, host to container
     */
    private List<String> ports;

    /**
     * Set of environment variables to pass to the container
     */
    private List<String> environment;

    /**
     * Set of volume mappings. Either docker volume to container directory, or host directory to container directory.
     */
    private List<Map<String, String>> volumes;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void addPortMapping(String mapping) {
        if(this.ports == null) {
            this.ports = new ArrayList<>();
        }
        this.ports.add(mapping);
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void addEnv(String env) {
        if(this.environment == null) {
            this.environment = new ArrayList<>();
        }
        this.environment.add(env);
    }

    public List<Map<String, String>> getVolumes() {
        return volumes;
    }

    public void addVolumeMapping(String mapping) {
        if(this.volumes == null) {
            this.volumes = new ArrayList<>();
        }
        final var kv = mapping.split(":", 2);
        this.volumes.add(Collections.singletonMap(kv[0], kv[1]));
    }
}
