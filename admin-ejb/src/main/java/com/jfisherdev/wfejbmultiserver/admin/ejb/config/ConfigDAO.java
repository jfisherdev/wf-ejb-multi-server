package com.jfisherdev.wfejbmultiserver.admin.ejb.config;

import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigProperty;
import com.jfisherdev.wfejbmultiserver.commons.EjbStringUtils;

/**
 * @author Josh Fisher
 */
public class ConfigDAO {

    public ConfigProperty getConfigProperty(String key, String defaultValue) {
        final String value = EjbStringUtils.trimToEmpty(System.getProperty(key, defaultValue));
        final ConfigProperty configProperty = new ConfigProperty();
        configProperty.setKey(key);
        configProperty.setValue(value);
        return configProperty;
    }
}
