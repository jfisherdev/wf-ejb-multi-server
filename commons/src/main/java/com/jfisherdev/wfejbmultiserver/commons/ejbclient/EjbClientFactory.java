package com.jfisherdev.wfejbmultiserver.commons.ejbclient;

import java.util.Properties;

/**
 * @author Josh Fisher
 */
public interface EjbClientFactory {

    EjbClient getEjbClient();

    EjbClient getEjbClient(String providerUrl);

    EjbClient getEjbClient(String providerUrl, Properties properties);
}
