package com.jfisherdev.wfejbmultiserver.customer.api;

import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClient;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClientFactoryLocator;

import javax.naming.NamingException;

/**
 * @author Josh Fisher
 */
public class CustomerClient {

    private final EjbClient ejbClient = new EjbClientFactoryLocator().getEjbClientFactory().getEjbClient();

    public CustomerClient() {
    }

    public Customer getCustomer(long id) {
        return getService().getCustomer(id);
    }

    public Customer addNewCustomer(String name) {
        return getService().addNewCustomer(name);
    }

    private CustomerServiceRemote getService() {
        try {
            return ejbClient.lookup(CustomerAppConstants.EJB_APP_NAME, CustomerAppConstants.EJB_MODULE_NAME, CustomerServiceRemote.BEAN_NAME, CustomerServiceRemote.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
