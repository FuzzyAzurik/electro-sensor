package dk.wortmann.electro.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

public class Worker implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Worker.class);
    private final Queue<Blink> queue;

    public Worker(Queue<Blink> queue) {
        this.queue = queue;

        Thread worker = new Thread(this);
        worker.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            deQueue().ifPresent((blink) -> {
                LOG.info("Processing blink: {}", blink);
            });
        }
    }

    private Optional<Blink> deQueue() {
            Blink blink = this.queue.poll();
            LOG.info("Removed blink: {} from queue", blink);

            return Optional.ofNullable(blink);
    }
}
