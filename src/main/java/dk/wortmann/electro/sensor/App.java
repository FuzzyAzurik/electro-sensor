package dk.wortmann.electro.sensor;


import com.pi4j.io.gpio.*;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class App {

    public static void main(String[] args) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        GpioController controller = GpioFactory.getInstance();
        GpioPinDigitalOutput pin0 = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, "GPIO_27");
        LinkedBlockingQueue<Blink> blinkQueue = new LinkedBlockingQueue<>();

//        Monitor lightMonitor = new Monitor(controller, pin0);
        Monitor lightMonitor = new Monitor(pin0, blinkQueue, -80);
        Worker worker = new Worker(blinkQueue, "Worker-1");
        Worker worker1 = new Worker(blinkQueue, "Worker-2");
    }
}
