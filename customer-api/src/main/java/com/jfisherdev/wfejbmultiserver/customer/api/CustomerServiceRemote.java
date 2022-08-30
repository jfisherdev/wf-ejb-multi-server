package com.jfisherdev.wfejbmultiserver.customer.api;

/**
 * @author Josh Fisher
 */
public interface CustomerServiceRemote {

    String BEAN_NAME = "Customer";

    Customer getCustomer(long id);

    Customer addNewCustomer(String name);

}
