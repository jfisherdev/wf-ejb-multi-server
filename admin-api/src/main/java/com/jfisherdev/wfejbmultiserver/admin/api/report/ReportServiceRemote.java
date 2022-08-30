package com.jfisherdev.wfejbmultiserver.admin.api.report;

import java.util.Set;

/**
 * @author Josh Fisher
 */
public interface ReportServiceRemote {

    String BEAN_NAME = "ReportService";

    ReportResponse submitReport(ReportRequest request);

    String asyncSubmitReport(ReportRequest request);

    ReportStatus getReportStatus(String jobId);

    Set<String> getRunningJobsForReport(String reportName);

}
