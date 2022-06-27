package at.kurumi.docker;

import at.kurumi.docker.entities.Container;
import at.kurumi.docker.entities.ContainerParameter;
import at.kurumi.logging.LoggingRouter;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


public class DockerInterface {

    private final LoggingRouter log;

    private DockerClient dockerClient;

    @Inject
    protected DockerInterface(LoggingRouter log) {
        this.log = log;
    }

    @PostConstruct
    protected void createClient() {
        dockerClient = DockerClientBuilder.getInstance().build();
    }

    @PreDestroy
    protected void destroyClient() {
        try {
            dockerClient.close();
        } catch (IOException e) {
            log.internalError("Closing Docker client", "Exception while closing. Resource may not have" +
                    "been released");
        }
    }

    public String newContainer(String resourceName, boolean start) {
        try (final var is = DockerInterface.class.getClassLoader().getResourceAsStream(resourceName);
             final var isr = new InputStreamReader(is);
             final var br = new BufferedReader(isr)) {
            final var container = parse(br.lines());

            if (container.isPresent()) {
                final var c = container.get();
                final var response = c.create(dockerClient);
                Arrays.stream(response.getWarnings()).forEach(warning -> log.internalWarn("Docker create",
                        warning));
                if(start) {
                    c.start(dockerClient);
                }

                return c.getName();
            }
            return "";
        } catch (FileNotFoundException | NullPointerException e) {
            log.internalError("Parse Container Config", "Could not find file");
            return "";
        } catch (IOException e) {
            log.internalError("Parse Container Config", "Could not read file");
            return "";
        }
    }



    private Optional<Container> parse(Stream<String> content) {
        final var container = new Container();
        final var parameter = new ContainerParameter();

        content.map(line -> line.split(":", 2))
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
