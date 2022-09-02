package com.jfisherdev.wfejbmultiserver.admin.ejb.config;

import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigProperty;
import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigServiceRemote;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Josh Fisher
 */
@Stateless(name = ConfigServiceRemote.BEAN_NAME)
@Remote(ConfigServiceRemote.class)
public class ConfigService implements ConfigServiceRemote {

    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());

    private final ConfigDAO configDAO = new ConfigDAO();

    @Override
    public ConfigProperty getConfigProperty(String key) {
        return getConfigProperty(key, "");
    }

    @Override
    public ConfigProperty getConfigProperty(String key, String defaultValue) {
        logger.info(() -> "Retrieving config property: {key=" + key + ", defaultValue=" + defaultValue + "}");
        final ConfigProperty configProperty = configDAO.getConfigProperty(key, defaultValue);
        logger.info(() -> "Retrieved config property: " + configProperty);
        return configProperty;
    }

}
