package com.jfisherdev.wfejbmultiserver.admin.api.report;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Josh Fisher
 */
public class ReportRequest implements Serializable {

    private String reportName = "";
    private ReportFormat format = ReportFormat.PLAIN;
    private Map<String, String> parameters = Collections.emptyMap();

    public ReportRequest() {
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = Objects.requireNonNull(reportName, "Report name may not be null");
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = Objects.requireNonNull(format, "Format may not be null");
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = Objects.requireNonNull(parameters, "Parameters may not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportRequest that = (ReportRequest) o;
        return reportName.equals(that.reportName) && format == that.format && parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, format, parameters);
    }

    @Override
    public String toString() {
        return "ReportRequest{" +
                "reportName='" + reportName + '\'' +
                ", format=" + format +
                ", parameters=" + parameters +
                '}';
    }
}
