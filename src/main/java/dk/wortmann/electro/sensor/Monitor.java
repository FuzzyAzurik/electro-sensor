package dk.wortmann.electro.sensor;

import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class Monitor implements Runnable {
    private final static Logger LOG = LogManager.getLogger(Monitor.class);
    private final static int READING_LIMIT = 15000;
    private final static int ARRAY_SIZE = 4;
    private final GpioPin pin;
    private final Queue<Reading> queue;
    private final double threshold;
    private ReadingsRingBuffer buffer;
    private boolean isBlinking;

    public Monitor(GpioPin pin, Queue<Reading> queue, double threshold) {
        this.pin = pin;
        this.queue = queue;
        this.threshold = threshold;
        this.buffer = new ReadingsRingBuffer(ARRAY_SIZE, READING_LIMIT);

        Thread producer = new Thread(this);
        producer.start();
    }

    private int readSensor() {
        int value = 0;
        pin.export(PinMode.DIGITAL_OUTPUT);
        ((GpioPinDigitalOutput) pin).setState(PinState.LOW);
        pin.export(PinMode.DIGITAL_INPUT);

        while (((GpioPinDigitalOutput) pin).getState() == PinState.LOW && value < READING_LIMIT) {
            value++;
        }

        return value;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int reading = this.readSensor();
            LOG.debug("Read value: {} Pin State: {}", reading, ((GpioPinDigitalOutput) pin).getState());
            double avgReading = this.buffer.getSum() / this.buffer.maxSize();
            double readingRatio = avgReading / reading;

            if (readingRatio > this.threshold) {
                if (!this.isBlinking) {
                    LOG.info("Blink");
                    LOG.debug("readingRatio: {}", readingRatio);
                    Reading readingItem = new Reading(reading, readingRatio);
                    if (queue.offer(readingItem)) {
                        this.startReTryWorker(readingItem);
                    }
                    this.isBlinking = true;
                }
            } else {
                this.isBlinking = false;
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
