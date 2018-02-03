package dk.wortmann.electro;


import com.pi4j.io.gpio.*;
import dk.wortmann.electro.sensor.model.Blink;
import dk.wortmann.electro.sensor.boundary.Monitor;
import dk.wortmann.electro.sensor.boundary.Worker;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

public class App {
    private static final Logger LOG = LogManager.getLogger(App.class);
    private static final String CONFIG_PATH = "config.xml";

    public static void main(String[] args) throws ConfigurationException {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        GpioController controller = GpioFactory.getInstance();
        XMLConfiguration config = new Configurations().xml(CONFIG_PATH);

        int gpio_pin = config.getInt("gpio_pin[@id]");
        GpioPinDigitalOutput pin0 = controller.provisionDigitalOutputPin(RaspiPin.getPinByAddress(gpio_pin), "GPIO_" + gpio_pin);

        // Queue
        LinkedBlockingQueue<Blink> blinkQueue = new LinkedBlockingQueue<>();

        Monitor lightMonitor = new Monitor(pin0, blinkQueue, config);
        Worker worker = new Worker(blinkQueue, "Worker-1", config);
        Worker worker1 = new Worker(blinkQueue, "Worker-2", config);
    }
}
