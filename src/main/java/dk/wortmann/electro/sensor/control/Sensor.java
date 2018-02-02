package dk.wortmann.electro.sensor.control;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sensor {
    private static final Logger LOG = LogManager.getLogger(Sensor.class);
    private final GpioPin pin;
    private final int readingLimit;

    public Sensor(GpioPin pin, int readingLimit) {
        this.pin = pin;
        this.readingLimit = readingLimit;
    }

    public int read() {
        int value = 0;
        pin.export(PinMode.DIGITAL_OUTPUT);
        ((GpioPinDigitalOutput) pin).setState(PinState.LOW);
        pin.export(PinMode.DIGITAL_INPUT);

        while (((GpioPinDigitalOutput) pin).getState() == PinState.LOW && value < readingLimit) {
            value++;
        }

        LOG.debug("Read value: {} Pin State: {}", value, ((GpioPinDigitalOutput) this.pin).getState());
        return value;
    }
}
