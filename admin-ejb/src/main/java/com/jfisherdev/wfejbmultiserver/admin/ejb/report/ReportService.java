package com.jfisherdev.wfejbmultiserver.admin.ejb.report;

import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportRequest;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportResponse;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportRun;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportServiceRemote;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportStatus;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josh Fisher
 */
@Stateless(name = ReportServiceRemote.BEAN_NAME)
@Remote(ReportServiceRemote.class)
public class ReportService implements ReportServiceRemote {

    private final ReportManager reportManager = ReportManager.getInstance();

    @Override
    public ReportResponse submitReport(ReportRequest request) {
        final String jobId = reportManager.submitReport(request);
        ReportRun reportRun = null;
        while (true) {
            boolean done = false;
            final Optional<ReportRun> reportRunCandidate = reportManager.getReportRun(jobId);
            if (reportRunCandidate.isPresent()) {
                reportRun = reportRunCandidate.get();
                switch (reportRun.getStatus()) {
                    case QUEUED:
                    case IN_PROGRESS:
                        continue;
                    case SUCCESS:
                    case FAILED:
                        done = true;
                        break;
                }
            }
            if (done) {
                break;
            }
        }
        final ReportResponse response = new ReportResponse();
        response.setJobId(jobId);
        response.setOutputData("OUTPUT- " + jobId);
        response.setSuccess(reportRun.isSuccess());

        return response;
    }

    @Override
    public String asyncSubmitReport(ReportRequest request) {
        return reportManager.submitReport(request);
    }

    @Override
    public ReportStatus getReportStatus(String jobId) {
        return reportManager.getReportRun(jobId).
                orElseThrow(()->new RuntimeException("No report found for job ID: " + jobId)).
                getStatus();
    }

    @Override
    public Set<String> getRunningJobsForReport(String reportName) {
        return reportManager.getRunningReports().stream().
                filter(reportRun -> reportName.equals(reportRun.getRequest().getReportName())).
                map(ReportRun::getJobId).collect(Collectors.toSet());
    }
}
