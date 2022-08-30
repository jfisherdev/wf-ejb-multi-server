package com.jfisherdev.wfejbmultiserver.admin.api.report;

import com.jfisherdev.wfejbmultiserver.admin.api.AdminAppConstants;
import com.jfisherdev.wfejbmultiserver.commons.EjbStringUtils;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClient;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClientFactory;
import com.jfisherdev.wfejbmultiserver.commons.ejbclient.EjbClientFactoryLocator;

import javax.naming.NamingException;
import java.util.Set;

/**
 * @author Josh Fisher
 */
public class ReportClient {
    private final EjbClient ejbClient;

    public ReportClient() {
        ejbClient = loadEjbClient();
    }

    public ReportResponse submitReport(ReportRequest request) {
        return getReportService().submitReport(request);
    }

    public String asyncSubmitReport(ReportRequest request) {
        return getReportService().asyncSubmitReport(request);
    }

    public ReportStatus getReportStatus(String jobId) {
        return getReportService().getReportStatus(jobId);
    }

    public Set<String> getRunningJobsForReport(String reportName) {
        return getReportService().getRunningJobsForReport(reportName);
    }

    private EjbClient loadEjbClient() {
        final String reportProviderUrl = System.getProperty("report.provider.url");
        final EjbClientFactory ejbClientFactory = new EjbClientFactoryLocator().getEjbClientFactory();
        if (EjbStringUtils.isPopulated(reportProviderUrl)) {
            return ejbClientFactory.getEjbClient(reportProviderUrl);
        }
        return ejbClientFactory.getEjbClient();
    }

    private ReportServiceRemote getReportService() {
        try {
            return ejbClient.lookup(AdminAppConstants.EJB_APP_NAME, AdminAppConstants.EJB_MODULE_NAME, ReportServiceRemote.BEAN_NAME, ReportServiceRemote.class);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
