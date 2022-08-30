package com.jfisherdev.wfejbmultiserver.customer.ejb;

import com.jfisherdev.wfejbmultiserver.customer.api.Customer;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerServiceRemote;

/**
 * @author Josh Fisher
 */
public class CustomerService implements CustomerServiceRemote {
    @Override
    public Customer getCustomer(long id) {
        return CustomerStore.getInstance().getCustomer(id).orElseThrow();
    }

    @Override
    public Customer addNewCustomer(String name) {
        return CustomerStore.getInstance().addNewCustomer(name);
    }
}
