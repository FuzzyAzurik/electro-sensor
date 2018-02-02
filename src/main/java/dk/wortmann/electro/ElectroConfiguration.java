package dk.wortmann.electro;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ElectroConfiguration {
    private static final String FILE_PATH = "config.xml";
    private static XMLConfiguration intance;

    public static XMLConfiguration getInstance() {
        if (intance == null) {
            try {
                intance = new Configurations().xml(FILE_PATH);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return intance;
    }
}
