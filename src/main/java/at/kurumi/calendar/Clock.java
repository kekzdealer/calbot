package at.kurumi.calendar;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple clock that does a task every minute.
 */
public class Clock {

    private final ScheduledExecutorService  executorService = new ScheduledThreadPoolExecutor(2);

    public ScheduledFuture<?> everyMinute(Runnable command) {
        return executorService.scheduleAtFixedRate(command, 0, 1, TimeUnit.MINUTES);
    }

    public void stop() {
        executorService.shutdownNow();
    }
}
