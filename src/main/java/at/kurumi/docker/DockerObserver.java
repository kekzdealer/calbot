package at.kurumi.docker;

import at.kurumi.docker.entities.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.io.Closeable;
import java.io.IOException;

public class DockerObserver {

    @Inject
    private Event<ContainerAvailableEvent> containerRunningEventEvent;

    public void observe(DockerClient dockerClient, Container container) {
        dockerClient.eventsCmd()
                .withContainerFilter(container.getName())
                .exec(new ResultCallback<com.github.dockerjava.api.model.Event>() {
            @Override
            public void onStart(Closeable closeable) {

            }

            @Override
            public void onNext(com.github.dockerjava.api.model.Event object) {
                System.out.println(object.getStatus());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void close() throws IOException {

            }
        });
    }


}
