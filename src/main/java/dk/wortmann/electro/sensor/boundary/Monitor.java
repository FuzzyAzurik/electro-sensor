package dk.wortmann.electro.sensor.boundary;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;

public class Monitor implements GpioPinListenerDigital{
    private final static Logger LOG = LogManager.getLogger(Monitor.class);
    private final XMLConfiguration config;
    private final Queue<Blink> queue;
    private LocalDateTime previousEvent;

    public Monitor(GpioPin pin, Queue<Blink> queue, XMLConfiguration config) {
        this.queue = queue;
        this.config = config;
        this.previousEvent = null;
        setupListener(pin);
    }

    private void setupListener(GpioPin pin) {
        LOG.info("setting eventListener on pin: {}", pin.getName());
        pin.addListener(this);
        pin.setShutdownOptions(true);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (event.getState().isHigh()) {
            LocalDateTime now = LocalDateTime.now();
            if (this.isBlink(now)) {
                LOG.info("Blink!");
                Blink blink = new Blink(100, this.config.getInt("meter[@id]"));
                this.queue.add(blink);
            }

            this.previousEvent = now;
        }
    }

    private Boolean isBlink(LocalDateTime now) {
        if (this.previousEvent == null) {
            return true;
        }

        try {
            long difference = ChronoUnit.MILLIS.between(this.previousEvent, now);
            LOG.debug("Previous: {}, Now: {}, Difference is: {}", previousEvent, now, difference);
            return difference > 200;
        } catch (ArithmeticException e) {
            // return true if difference causes overflow.
            return true;
        }
    }
}
