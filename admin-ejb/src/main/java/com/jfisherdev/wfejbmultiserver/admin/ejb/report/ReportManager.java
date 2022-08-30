package com.jfisherdev.wfejbmultiserver.admin.ejb.report;

import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportRequest;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportRun;
import com.jfisherdev.wfejbmultiserver.admin.api.report.ReportStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Josh Fisher
 */
class ReportManager {

    private static final int MIN_TIME = 1;
    private static final int MAX_TIME = 60;

    private static final Logger logger = Logger.getLogger(ReportManager.class.getName());

    private class RunReportCallable implements Callable<ReportRun> {

        private final Random rng = new Random();

        private final ReportRun reportRun;

        private RunReportCallable(ReportRun reportRun) {
            this.reportRun = reportRun;
        }

        @Override
        public ReportRun call() throws Exception {
            final String jobId = reportRun.getJobId();
            runningJobs.put(jobId, reportRun);
            reportRun.setStatus(ReportStatus.IN_PROGRESS);
            final Instant startTime = Instant.now();
            reportRun.setStartTime(startTime);
            final int runTime = rng.nextInt(MAX_TIME - MIN_TIME) + 1;
            Thread.sleep(TimeUnit.SECONDS.toMillis(runTime));
            final Instant endTime = Instant.now();
            reportRun.setEndTime(endTime);
            final boolean success = rng.nextBoolean();
            reportRun.setSuccess(success);
            reportRun.setStatus(success ? ReportStatus.SUCCESS : ReportStatus.FAILED);
            runningJobs.remove(jobId);
            completedRuns.add(reportRun);
            return reportRun;
        }
    }

    private static class ReportRunJobIdMatches implements Predicate<ReportRun> {

        private final String jobId;

        private ReportRunJobIdMatches(String jobId) {
            this.jobId = jobId;
        }

        @Override
        public boolean test(ReportRun reportRun) {
            return jobId.equals(reportRun.getJobId());
        }
    }


    private static class Holder {
        static final ReportManager INSTANCE = new ReportManager();
    }

    static ReportManager getInstance() {
        return Holder.INSTANCE;
    }

    private final ExecutorService reportQueueHandler = Executors.newSingleThreadExecutor();
    private final ExecutorService reportRunExecutor = Executors.newFixedThreadPool(5);


    private final BlockingQueue<ReportRun> reportRunQueue = new LinkedBlockingQueue<>(5);
    private final Map<String, ReportRun> runningJobs = new ConcurrentHashMap<>();
    private final Set<ReportRun> completedRuns = new LinkedHashSet<>();

    ReportManager() {
        reportQueueHandler.submit(new Runnable() {
            @Override
            public void run() {
                Optional<ReportRun> nextReportRunCandidate;
                while (true) {
                    try {
                        while ((nextReportRunCandidate = getNextRun()).isPresent()) {
                            final ReportRun reportRun = nextReportRunCandidate.get();
                            reportRunExecutor.submit(new RunReportCallable(reportRun));
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Exception processing report run queue task", e);
                    }
                }
            }
        });
    }

    String submitReport(ReportRequest request) {
        final String jobId = UUID.randomUUID().toString();
        final ReportRun reportRun = new ReportRun();
        reportRun.setRequest(request);
        reportRun.setJobId(jobId);
        reportRun.setStatus(ReportStatus.QUEUED);
        reportRunQueue.offer(reportRun);
        return jobId;
    }

    Set<ReportRun> getRunningReports() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(runningJobs.values()));
    }

    Optional<ReportRun> getReportRun(String jobId) {
        final ReportRunJobIdMatches jobIdMatches = new ReportRunJobIdMatches(jobId);
        final Optional<ReportRun> maybeInQueue = reportRunQueue.stream().filter(jobIdMatches).findFirst();
        if (maybeInQueue.isPresent()) {
            return maybeInQueue;
        }
        final Optional<ReportRun> maybeRunning = Optional.ofNullable(runningJobs.get(jobId));
        if (maybeRunning.isPresent()) {
            return maybeRunning;
        }
        return completedRuns.stream().filter(jobIdMatches).findFirst();
    }

    void shutdown() {
        reportQueueHandler.shutdown();
        reportRunExecutor.shutdown();
    }

    private Optional<ReportRun> getNextRun() {
        try {
            return Optional.of(reportRunQueue.take());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }


}
