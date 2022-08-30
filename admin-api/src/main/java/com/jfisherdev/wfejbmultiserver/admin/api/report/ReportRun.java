package com.jfisherdev.wfejbmultiserver.admin.api.report;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Josh Fisher
 */
public class ReportRun implements Serializable {

    private String jobId;
    private ReportRequest request;
    private ReportStatus status;
    private boolean success;
    private Instant startTime;
    private Instant endTime;

    public ReportRun() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ReportRequest getRequest() {
        return request;
    }

    public void setRequest(ReportRequest request) {
        this.request = request;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportRun reportRun = (ReportRun) o;
        return success == reportRun.success && jobId.equals(reportRun.jobId) && request.equals(reportRun.request) && status == reportRun.status && Objects.equals(startTime, reportRun.startTime) && Objects.equals(endTime, reportRun.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, request, status, success, startTime, endTime);
    }

    @Override
    public String toString() {
        return "ReportRun{" +
                "jobId='" + jobId + '\'' +
                ", request=" + request +
                ", status=" + status +
                ", success=" + success +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
