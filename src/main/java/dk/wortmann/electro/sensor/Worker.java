package dk.wortmann.electro.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

public class Worker implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Worker.class);
    private final Queue<Reading> queue;

    public Worker(Queue<Reading> queue) {
        this.queue = queue;

        Thread worker = new Thread(this);
        worker.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            deQueue().ifPresent((reading) -> {
                LOG.info("Processing reading: {}", reading);
            });
        }
    }

    private Optional<Reading> deQueue() {
        try {
            Reading reading = this.queue.remove();
            LOG.info("Removed reading: {} from queue", reading);

            return Optional.ofNullable(reading);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
}
