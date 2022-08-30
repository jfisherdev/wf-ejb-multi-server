package com.jfisherdev.wfejbmultiserver.commons.ejbclient;

import java.util.Properties;

/**
 * @author Josh Fisher
 */
public class DefaultEjbClientFactory implements EjbClientFactory {
    @Override
    public EjbClient getEjbClient() {
        return new EjbClient();
    }

    @Override
    public EjbClient getEjbClient(String providerUrl) {
        return new EjbClient(providerUrl);
    }

    @Override
    public EjbClient getEjbClient(String providerUrl, Properties properties) {
        return new EjbClient(providerUrl, properties);
    }
}
