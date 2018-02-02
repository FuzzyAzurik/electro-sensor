package dk.wortmann.electro.sensor.boundary;

import com.pi4j.io.gpio.*;
import dk.wortmann.electro.sensor.control.BlinkController;
import dk.wortmann.electro.sensor.control.BlinkRingBuffer;
import dk.wortmann.electro.sensor.control.Sensor;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class Monitor implements Runnable {
    private final static Logger LOG = LogManager.getLogger(Monitor.class);
    private final static int READING_LIMIT = 15000;
    private final static int ARRAY_SIZE = 4;
    private final Queue<Blink> queue;
    private final Sensor sensor;
    private final BlinkRingBuffer buffer;
    private final BlinkController blinkController;

    public Monitor(GpioPin pin, Queue<Blink> queue, double threshold) {
        this.queue = queue;
        this.buffer = new BlinkRingBuffer(ARRAY_SIZE, READING_LIMIT);
        this.sensor = new Sensor(pin, READING_LIMIT);
        this.blinkController = new BlinkController(threshold);
        Thread producer = new Thread(this,"Monitor");
        producer.start();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int reading = this.sensor.read();

            if (this.blinkController.isBlinking(reading, this.buffer)) {
                Blink blinkItem = new Blink(reading, 99806);
                if (!queue.offer(blinkItem)) {
                    this.startReTryWorker(blinkItem);
                }
            }

            this.buffer.add(reading);
        }
    }



    private void startReTryWorker(Blink blinkItem) {
        LOG.warn("Unable to add reading: {} to the queue", blinkItem);

        Runnable retryWorker = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                LOG.info("retrying to add reading: {} to queue", blinkItem);
                this.queue.offer(blinkItem);
            }
        };
        retryWorker.run();
    }
}
