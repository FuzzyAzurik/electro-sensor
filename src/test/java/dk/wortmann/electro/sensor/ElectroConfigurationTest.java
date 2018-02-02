package dk.wortmann.electro.sensor;

import org.apache.commons.configuration2.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElectroConfigurationTest {

    XMLConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = ElectroConfiguration.getInstance();
    }

    @Test
    public void getElement() {
        String exptected = "GPIO_27";

        String gpio_pin = config.getString("gpio");

        assertEquals(exptected, gpio_pin);
    }

    @Test
    public void getNestedElem() {
        int expected = -80;

        int result = config.getInt("controller.threshold");

        assertEquals(expected, result);
    }

    @Test
    public void getAttrOfElem() {
        int expected = 99806;

        int result = config.getInt("meter[@id]");

        assertEquals(expected, result);
    }
}