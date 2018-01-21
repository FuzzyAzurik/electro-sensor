package dk.wortmann.electro.sensor;


import com.pi4j.io.gpio.*;

public class App {

    public static void main(String[] args) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        GpioController controller = GpioFactory.getInstance();
        GpioPinDigitalOutput pin0 = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, "GPIO_27");

//        Monitor lightMonitor = new Monitor(controller, pin0);
        Monitor lightMonitor = new Monitor(controller, pin0);
        Thread t = new Thread(lightMonitor);
        t.start();
    }
}
