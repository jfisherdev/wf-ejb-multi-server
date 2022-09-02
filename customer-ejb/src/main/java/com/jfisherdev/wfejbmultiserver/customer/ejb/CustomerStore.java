package com.jfisherdev.wfejbmultiserver.customer.ejb;

import com.jfisherdev.wfejbmultiserver.customer.api.Customer;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerSatisfactionRating;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Josh Fisher
 */
class CustomerStore {

    private static final Set<Customer> DEFAULT_CUSTOMERS;

    static {
        DEFAULT_CUSTOMERS = new LinkedHashSet<>();
        final Customer defaultCustomer1 = new Customer();
        defaultCustomer1.setId(1);
        defaultCustomer1.setName("C1");
        final Customer defaultCustomer2 = new Customer();
        defaultCustomer2.setId(2);
        defaultCustomer2.setName("C2");

        DEFAULT_CUSTOMERS.add(defaultCustomer1);
        DEFAULT_CUSTOMERS.add(defaultCustomer2);
    }

    private static class Holder {
        static final CustomerStore INSTANCE = new CustomerStore();
    }

    static CustomerStore getInstance() {
        return Holder.INSTANCE;
    }

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Set<CustomerSatisfactionRating> satisfactionRatings = new LinkedHashSet<>();
    private final AtomicLong customerSequence = new AtomicLong(3);

    CustomerStore() {
        for (Customer defaultCustomer : DEFAULT_CUSTOMERS) {
            customers.put(defaultCustomer.getId(), defaultCustomer);
        }
    }

    Optional<Customer> getCustomer(long id) {
        return Optional.ofNullable(customers.get(id));
    }

    Customer addNewCustomer(String name) {
        final Customer newCustomer = new Customer();
        newCustomer.setId(customerSequence.incrementAndGet());
        newCustomer.setName(name);
        return newCustomer;
    }

    CustomerSatisfactionRating addRating(CustomerSatisfactionRating rating) {
        satisfactionRatings.add(rating);
        return rating;
    }

    Set<CustomerSatisfactionRating> getSatisfactionRatingsForCustomer(long id) {
        return satisfactionRatings.stream().filter(rating -> id == rating.getCustomerId()).
                sorted(Comparator.comparing(CustomerSatisfactionRating::getRatingTime)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
