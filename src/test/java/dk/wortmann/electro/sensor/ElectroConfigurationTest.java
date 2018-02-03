package dk.wortmann.electro.sensor;

import dk.wortmann.electro.ElectroConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElectroConfigurationTest {

    XMLConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = ElectroConfiguration.getInstance("config-default.xml");
    }

    @Test
    public void getNestedElem() {
        int expected = -80;

        int result = config.getInt("controller.threshold");

        assertEquals(expected, result);
    }

    @Test
    public void getAttrOfElem() {
        int expected = 1;

        int result = config.getInt("meter[@id]");

        assertEquals(expected, result);
    }
}