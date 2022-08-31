package com.jfisherdev.wfejbmultiserver.customer.api;

/**
 * @author Josh Fisher
 */
public interface CustomerServiceRemote {

    String BEAN_NAME = "Customer";

    Customer getCustomer(long id);

    Customer addNewCustomer(String name);

    String getCustomerSatisfactionRating(long id, boolean assess, boolean verbose);

    //This one can/will break in a multi-server environment if admin report calls are sent to another server
    String getCustomerSatisfactionRatingV1(long id);

    //This one does not fail, but will keep admin report calls local even configured to go to another server
    String getCustomerSatisfactionRatingV2(long id);

}
