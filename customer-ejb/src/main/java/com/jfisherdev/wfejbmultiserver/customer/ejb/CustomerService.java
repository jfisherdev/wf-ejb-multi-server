package com.jfisherdev.wfejbmultiserver.customer.ejb;

import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigProperty;
import com.jfisherdev.wfejbmultiserver.admin.api.config.ConfigPropertyClient;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportClient;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportFormat;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportRequest;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportResponse;
import com.jfisherdev.wfejbmultiserver.customer.api.Customer;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerSatisfactionRating;
import com.jfisherdev.wfejbmultiserver.customer.api.CustomerServiceRemote;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Josh Fisher
 */
@Stateless(name = CustomerServiceRemote.BEAN_NAME)
@Remote(CustomerServiceRemote.class)
public class CustomerService implements CustomerServiceRemote {

    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    private final ConfigPropertyClient configPropertyClient = new ConfigPropertyClient();
    private final ReportClient reportClient = new ReportClient();
    private final Random rng = new Random();

    @Override
    public Customer getCustomer(long id) {
        return CustomerStore.getInstance().getCustomer(id).orElseThrow();
    }

    @Override
    public Customer addNewCustomer(String name) {
        return CustomerStore.getInstance().addNewCustomer(name);
    }

    @Override
    public String getCustomerSatisfactionRating(long id, boolean assess, boolean verbose) {
        final int level = rng.nextInt(9) + 1;
        addRating(id, level);
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Level = ").append(level);
        String details = "";
        if (verbose) {
            details = getRatingReport(id);
        }
        if (assess) {
            responseBuilder.append(", ").append(assessRating(level));
        }
        if (!details.isEmpty()) {
            responseBuilder.append(", Details=").append(details);
        }
        return responseBuilder.toString();
    }

    @Override
    public String getCustomerSatisfactionRatingV1(long id) {
        logger.info("Starting customer satisfaction rating and assessment (V1)");
        //This can/will fail in a multi-server environment if admin report calls are configured to go to another server
        //In this case, the first admin app call goes to the remote server as expected, but the second one that should stay
        //local tries to go to the remote server and fails.
        final int level = rng.nextInt(9) + 1;
        addRating(id, level);
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Level = ").append(level);
        final String details = getRatingReport(id);
        final String assessedLevel = assessRating(level);
        responseBuilder.append(", ").append(assessedLevel);
        responseBuilder.append(", Details=").append(details);
        logger.info("Finished customer satisfaction rating and assessment (V1)");
        return responseBuilder.toString();
    }

    @Override
    public String getCustomerSatisfactionRatingV2(long id) {
        logger.info("Starting customer satisfaction rating and assessment (V2)");
        //This does not fail in a multi-server environment if admin report calls are configured to go another server; however,
        //the report calls do not end up going to the remote server as expected. In this case, the first admin app call
        //stays local as expected, but the second one that should go to the remote server also stays local.
        final int level = rng.nextInt(9) + 1;
        addRating(id, level);
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Level = ").append(level);
        final String assessedLevel = assessRating(level);
        final String details = getRatingReport(id);
        responseBuilder.append(", ").append(assessedLevel);
        responseBuilder.append(", Details=").append(details);
        logger.info("Finished customer satisfaction rating and assessment (V2)");
        return responseBuilder.toString();
    }

    @Override
    public Set<CustomerSatisfactionRating> getCustomerSatisfactionRatingHistory(long id) {
        return CustomerStore.getInstance().getSatisfactionRatingsForCustomer(id);
    }

    private void addRating(long id, int rating) {
        final CustomerSatisfactionRating ratingEntry = new CustomerSatisfactionRating();
        ratingEntry.setCustomerId(id);
        ratingEntry.setRating(rating);
        ratingEntry.setRatingTime(Instant.now());
        CustomerStore.getInstance().addRating(ratingEntry);
    }

    private String getRatingReport(long id) {
        final ReportRequest request = new ReportRequest();
        request.setFormat(ReportFormat.PLAIN);
        request.setParameters(Collections.singletonMap("id", Long.toString(id)));
        request.setReportName("CSR");
        final ReportResponse response = reportClient.submitReport(request);
        return "CSR REPORT INFO: " + response.getOutputData();
    }

    private String assessRating(int level) {
        final ConfigProperty thresholdProperty = configPropertyClient.getConfigProperty("csr.threshold");
        int threshold = 5;
        if (thresholdProperty.hasValue()) {
            try {
                threshold = Integer.parseInt(thresholdProperty.getValue());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to parse " + thresholdProperty.getKey() + " value: " + thresholdProperty.getValue());
            }
        }
        return String.format("Satisfied=%s", level >= threshold);
    }
}
