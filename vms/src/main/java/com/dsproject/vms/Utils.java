package com.dsproject.vms;

import java.io.IOException;
import java.util.Properties;

public class Utils {

    public static Properties loadProperties(final String propFile) {
        final Properties properties = new Properties();
        try {
            properties.load(
                    Utils.class.getClassLoader().getResourceAsStream(propFile)
            );
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
