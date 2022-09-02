package com.jfisherdev.wfejbmultiserver.customer.api;

import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClient;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClientFactoryLocator;

import javax.naming.NamingException;
import java.util.Set;

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

    public String getCustomerSatisfactionRating(long id, boolean assess, boolean verbose) {
        return getService().getCustomerSatisfactionRating(id, assess, verbose);
    }

    public String getCustomerSatisfactionRatingV1(long id) {
        return getService().getCustomerSatisfactionRatingV1(id);
    }

    public String getCustomerSatisfactionRatingV2(long id) {
        return getService().getCustomerSatisfactionRatingV2(id);
    }

    public Set<CustomerSatisfactionRating> getCustomerSatisfactionRatingHistory(long id) {
        return getService().getCustomerSatisfactionRatingHistory(id);
    }

    private CustomerServiceRemote getService() {
        try {
            return ejbClient.lookup(CustomerAppConstants.EJB_APP_NAME, CustomerAppConstants.EJB_MODULE_NAME, CustomerServiceRemote.BEAN_NAME, CustomerServiceRemote.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
