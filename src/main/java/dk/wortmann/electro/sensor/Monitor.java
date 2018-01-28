package dk.wortmann.electro.sensor;

import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class Monitor implements Runnable {
    private final static Logger LOG = LogManager.getLogger(Monitor.class);
    private final static int READING_LIMIT = 15000;
    private final static int ARRAY_SIZE = 4;
    private final Queue<Reading> queue;
    private final Sensor sensor;
    private final ReadingRingBuffer buffer;
    private final BlinkController blinkController;

    public Monitor(GpioPin pin, Queue<Reading> queue, double threshold) {
        this.queue = queue;
        this.buffer = new ReadingRingBuffer(ARRAY_SIZE, READING_LIMIT);
        this.sensor = new Sensor(pin, READING_LIMIT);
        this.blinkController = new BlinkController(threshold);
        Thread producer = new Thread(this);
        producer.start();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int reading = this.sensor.read();

            if (this.blinkController.isBlinking(reading, this.buffer)) {
                Reading readingItem = new Reading(reading);
                if (!queue.offer(readingItem)) {
                    this.startReTryWorker(readingItem);
                }
            }

            this.buffer.add(reading);
        }
    }



    private void startReTryWorker(Reading readingItem) {
        LOG.warn("Unable to add reading: {} to the queue", readingItem);

        Runnable retryWorker = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                LOG.info("retrying to add reading: {} to queue", readingItem);
                this.queue.offer(readingItem);
            }
        };
        retryWorker.run();
    }
}
