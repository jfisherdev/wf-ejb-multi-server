package com.jfisherdev.wfejbmultiserver.admin.api.report;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Josh Fisher
 */
public class ReportResponse implements Serializable {

    private String jobId = "";
    private String outputData = "";
    private boolean success;

    public ReportResponse() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportResponse that = (ReportResponse) o;
        return success == that.success && jobId.equals(that.jobId) && outputData.equals(that.outputData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, outputData, success);
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "jobId='" + jobId + '\'' +
                ", outputData='" + outputData + '\'' +
                ", success=" + success +
                '}';
    }
}
