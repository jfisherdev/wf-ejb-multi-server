package com.jfisherdev.wfejbmultiserver.commons.ejbclient;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Josh Fisher
 */
public class EjbClientFactoryLocator {

    public EjbClientFactory getEjbClientFactory() {
        final Iterator<EjbClientFactory> spiFactoriesIterator = ServiceLoader.load(EjbClientFactory.class).iterator();
        if (spiFactoriesIterator.hasNext()) {
            return spiFactoriesIterator.next();
        }
        if ("cached".equals(System.getProperty("ejbclient.factory"))) {
            return CachedEjbClientFactory.getInstance();
        }
        return new DefaultEjbClientFactory();
    }


}
