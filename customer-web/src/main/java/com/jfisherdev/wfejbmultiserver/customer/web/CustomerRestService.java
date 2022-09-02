package com.jfisherdev.wfejbmultiserver.customer.web;

import com.jfisherdev.wfejbmultiserver.customer.api.Customer;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerClient;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerSatisfactionRating;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * @author Josh Fisher
 */
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    private final CustomerClient customerClient = new CustomerClient();

    @GET
    @Path("{id}")
    public Customer getCustomer(@PathParam("id") long id) {
        return customerClient.getCustomer(id);
    }

    @POST
    public Customer addNewCustomer(@FormParam("name") String name) {
        return customerClient.addNewCustomer(name);
    }

    @GET
    @Path("/{id}/rating")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCustomerSatisfactionRating(@PathParam("id") long id,
                                                @DefaultValue("true") @QueryParam("assess") boolean assess,
                                                @DefaultValue("false") @QueryParam("verbose") boolean verbose) {
        return customerClient.getCustomerSatisfactionRating(id, assess, verbose);
    }

    @GET
    @Path("/{id}/rating/v1")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCustomerSatisfactionRatingV1(@PathParam("id") long id) {
        return customerClient.getCustomerSatisfactionRatingV1(id);
    }

    @GET
    @Path("/{id}/rating/v2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCustomerSatisfactionRatingV2(@PathParam("id") long id) {
        return customerClient.getCustomerSatisfactionRatingV2(id);
    }

    @GET
    @Path("/{id}/rating/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<CustomerSatisfactionRating> getCustomerSatisfactionRatingHistory(@PathParam("id") long id) {
        return customerClient.getCustomerSatisfactionRatingHistory(id);
    }

}
