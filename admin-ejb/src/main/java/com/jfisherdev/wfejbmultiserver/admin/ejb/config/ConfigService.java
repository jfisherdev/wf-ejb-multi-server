package com.jfisherdev.wfejbmultiserver.admin.ejb.config;

import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigProperty;
import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigServiceRemote;

import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * @author Josh Fisher
 */
@Stateless(name = ConfigServiceRemote.BEAN_NAME)
@Remote(ConfigServiceRemote.class)
public class ConfigService implements ConfigServiceRemote {

    private final ConfigDAO configDAO = new ConfigDAO();

    @Override
    public ConfigProperty getConfigProperty(String key) {
        return configDAO.getConfigProperty(key);
    }

}
