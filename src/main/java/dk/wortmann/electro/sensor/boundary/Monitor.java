package dk.wortmann.electro.sensor.boundary;

import com.pi4j.io.gpio.*;
import dk.wortmann.electro.ElectroConfiguration;
import dk.wortmann.electro.sensor.control.BlinkController;
import dk.wortmann.electro.sensor.control.BlinkRingBuffer;
import dk.wortmann.electro.sensor.control.Sensor;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class Monitor implements Runnable {
    private final static Logger LOG = LogManager.getLogger(Monitor.class);
    private final Queue<Blink> queue;
    private final XMLConfiguration config;
    private final Sensor sensor;
    private final BlinkRingBuffer buffer;
    private final BlinkController blinkController;

    public Monitor(GpioPin pin, Queue<Blink> queue, XMLConfiguration config) {
        this.queue = queue;
        this.config = config;
        this.buffer = new BlinkRingBuffer(this.config.getInt("ringBuffer.size"), this.config.getInt("sensor.readingLimit"));
        this.sensor = new Sensor(pin, this.config.getInt("sensor.readingLimit"));
        this.blinkController = new BlinkController(this.config.getInt("controller.threshold"), this.config.getInt("controller.thresholdSum"));

        Thread producer = new Thread(this,"Monitor");
        producer.start();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int reading = this.sensor.read();

            if (this.blinkController.isBlinking(reading, this.buffer)) {
                Blink blinkItem = new Blink(reading, this.config.getInt("meter[@id]"));
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
