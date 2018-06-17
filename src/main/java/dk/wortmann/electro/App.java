package dk.wortmann.electro;


import com.pi4j.io.gpio.*;
import dk.wortmann.electro.sensor.boundary.Monitor;
import dk.wortmann.electro.sensor.boundary.Worker;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
    private static final Logger LOG = LogManager.getLogger(App.class);
    private static final String CONFIG_PATH = "config.xml";
    private static int workerIdx = 0;


    public static void main(String[] args) throws ConfigurationException, InterruptedException {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        GpioController controller = GpioFactory.getInstance();
        XMLConfiguration config = new Configurations().xml(CONFIG_PATH);
        int pinNumber = config.getInt("gpio_pin[@id]");
        GpioPin pin = controller.provisionDigitalMultipurposePin(RaspiPin.getPinByAddress(pinNumber), PinMode.DIGITAL_INPUT, PinPullResistance.PULL_DOWN);
        pin.setName("GPIO_" + pinNumber);

        // Queue
        LinkedBlockingQueue<Blink> blinkQueue = new LinkedBlockingQueue<>();
        Monitor monitor = new Monitor(pin, blinkQueue, config);
        List<Worker> workers = new ArrayList<>();

        // keep program running until user aborts (CTRL-C)
        while (true) {
            Thread.sleep(5000);
            if (workers.isEmpty() || blinkQueue.size() / workers.size() >= 3) {
                spawnWorker(workers, blinkQueue, config);
            } else if (!workers.isEmpty() && blinkQueue.size() > workers.size()) {
                removeWorker(workers);
            }
        }
    }

    private static void spawnWorker(List<Worker> workers, LinkedBlockingQueue<Blink> blinkQueue, XMLConfiguration config) {
        Worker worker = new Worker(blinkQueue, "Worker-" + ++workerIdx, config);
        workers.add(worker);
    }


    private static void removeWorker(List<Worker> workers) {
        Worker worker = workers.get(workers.size() - 1);
        worker.stop();
        workers.remove(worker);
    }
}
