package com.jfisherdev.wfejbmultiserver.admin.api.config;

import com.jfisherdev.wfejbmultiserver.admin.api.AdminAppConstants;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClient;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClientFactoryLocator;

import javax.naming.NamingException;

/**
 * @author Josh Fisher
 */
public class ConfigPropertyClient {

    private final EjbClient ejbClient = new EjbClientFactoryLocator().getEjbClientFactory().getEjbClient();

    public ConfigPropertyClient() {
    }

    public ConfigProperty getConfigProperty(String key) {
        return getConfigService().getConfigProperty(key);
    }

    private ConfigServiceRemote getConfigService() {
        try {
            return ejbClient.lookup(AdminAppConstants.EJB_APP_NAME, AdminAppConstants.EJB_MODULE_NAME, ConfigServiceRemote.BEAN_NAME, ConfigServiceRemote.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
