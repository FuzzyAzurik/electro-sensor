package dk.wortmann.electro;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ElectroConfiguration {
    private static final String FILE_PATH = "config.xml";
    private static XMLConfiguration intance;

    public static XMLConfiguration getInstance(String filePath) {
        if (intance == null || !FILE_PATH.equalsIgnoreCase(filePath)) {
            try {
                intance = new Configurations().xml(filePath);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return intance;
    }

    public static XMLConfiguration getInstance() {
        return ElectroConfiguration.getInstance("config.xml");
    }
}
