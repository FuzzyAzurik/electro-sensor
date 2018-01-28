package dk.wortmann.electro.sensor;


import com.pi4j.io.gpio.*;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class App {

    public static void main(String[] args) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        GpioController controller = GpioFactory.getInstance();
        GpioPinDigitalOutput pin0 = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, "GPIO_27");
        Queue<Reading> readingQueue = new LinkedBlockingDeque<>();

//        Monitor lightMonitor = new Monitor(controller, pin0);
        Monitor lightMonitor = new Monitor(pin0, readingQueue, 1.25);
        Worker worker = new Worker(readingQueue);
    }
}
