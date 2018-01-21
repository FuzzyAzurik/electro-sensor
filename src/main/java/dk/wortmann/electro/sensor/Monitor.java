package dk.wortmann.electro.sensor;

import com.pi4j.io.gpio.*;

public class Monitor implements Runnable {

    private final static int READING_LIMIT = 15000;
    private final GpioPin pin;
    private final GpioController controller;

    public Monitor(GpioController controller, GpioPin pin) {
        this.controller = controller;
        this.pin = pin;
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
        while (true) {
            int reading = this.readSensor();

            System.out.println("Value: " + reading);
            System.out.println("Pin state: " + ((GpioPinDigitalOutput) pin).getState());
        }
    }
}
