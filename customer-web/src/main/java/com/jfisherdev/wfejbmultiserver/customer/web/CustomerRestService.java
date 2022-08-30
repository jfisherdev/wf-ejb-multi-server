package com.jfisherdev.wfejbmultiserver.customer.web;

import com.jfisherdev.wfejbmultiserver.customer.api.Customer;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerClient;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.PageAttributes;

/**
 * @author Josh Fisher
 */
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    private final CustomerClient customerClient = new CustomerClient();

    @GET
    @Path("{id}")
    public Customer getCustomer(@PathParam("id") Long id) {
        return customerClient.getCustomer(id);
    }

    @POST
    public Customer addNewCustomer(@FormParam("name") String name) {
        return customerClient.addNewCustomer(name);
    }

}
