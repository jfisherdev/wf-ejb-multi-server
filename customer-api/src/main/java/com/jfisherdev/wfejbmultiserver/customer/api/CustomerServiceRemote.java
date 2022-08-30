package com.jfisherdev.wfejbmultiserver.customer.api;

/**
 * @author Josh Fisher
 */
public interface CustomerServiceRemote {

    String BEAN_NAME = "Customer";

    Customer getCustomer(long id);

    Customer addNewCustomer(String name);

    String getCustomerSatisfactionRating(long id, boolean assess, boolean verbose);

    String getCustomerSatisfactionRatingV1(long id);

    String getCustomerSatisfactionRatingV2(long id);

}
