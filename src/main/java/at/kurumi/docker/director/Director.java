package at.kurumi.docker.director;

import at.kurumi.logging.LoggingRouter;
import com.github.dockerjava.core.DockerClientBuilder;
import io.netty.util.internal.StringUtil;
import jakarta.inject.Inject;

import java.io.*;
import java.util.Optional;
import java.util.Set;

public class Director {

    @Inject LoggingRouter log;

    public void createContainersFromConfiguration(String rootPath) {
        final var dockerClient = DockerClientBuilder.getInstance().build();

        collectConfigurationFiles(rootPath).stream()
                .map(this::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(container -> container.create(dockerClient))
                .forEach(response -> log.internalInfo("Create Container", response.toString()));

    }

    public void startContainers() {

    }

    private Set<File> collectConfigurationFiles(String rootPath) {
        return null;
    }

    private Optional<Container> parse(File file) {
        final var container = new Container();
        final var parameter = new ContainerParameter();

        try (var reader = new FileReader(file);
             var br = new BufferedReader(reader)) {
            br.lines()
                    .map(line -> line.split(":", 2))
                    .forEach(kv -> {
                        var v = kv[1];
                        switch(kv[0]) {
                            case "depends_on": container.addDependency(v); break;
                            case "image": parameter.setImage(v); break;
                            case "container_name": parameter.setContainerName(v); break;
                            case "port": parameter.addPortMapping(v); break;
                            case "env": parameter.addEnv(v); break;
                            case "volume": parameter.addVolumeMapping(v); break;
                            // TODO: This should really be an error and cancel the parse process but idk how to get out of here
                            default: log.internalWarn("Parse Container Config", "Unknown key: " + kv[0]);
                        }
                    });
        } catch (FileNotFoundException e) {
            log.internalError("Parse Container Config", "Could not find file");
        } catch (IOException e) {
            log.internalError("Parse Container Config", "Could not read file");
        }

        if(StringUtil.isNullOrEmpty(parameter.getImage())) {
            log.internalError("Parse Container Config", "Missing image name");
            return Optional.empty();
        }

        if(StringUtil.isNullOrEmpty(parameter.getContainerName())) {
            log.internalError("Parse Container Config", "Missing container name");
            return Optional.empty();
        }

        container.setParameter(parameter);
        return Optional.of(container);
    }

}
