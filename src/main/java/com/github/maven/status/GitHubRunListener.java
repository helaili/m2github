package com.github.maven.status;

import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.surefire.report.ReportEntry;
import org.codehaus.plexus.component.annotations.Component;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

@Component(role = org.junit.runner.notification.RunListener.class, hint = "github-runner-listener")
public class GitHubRunListener extends org.junit.runner.notification.RunListener implements org.apache.maven.surefire.report.RunListener {

	public GitHubRunListener() {
        System.out.println("Creation of Run Listener...");
    }
	
	public void testAssumptionFailure(ReportEntry arg0) {
		System.out.println("*********** TEST ASSUMPTION FAILURE **************");

	}

	public void testError(ReportEntry arg0) {
		System.out.println("*********** TEST ERROR **************");

	}

	public void testExecutionSkippedByUser() {
		System.out.println("*********** TEST SKIPPED BY USER **************");

	}

	public void testFailed(ReportEntry arg0) {
		System.out.println("*********** TEST FAILED **************");

	}

	public void testSetCompleted(ReportEntry arg0) {
		System.out.println("*********** TEST SET COMPLETED **************");

	}

	public void testSetStarting(ReportEntry arg0) {
		System.out.println("*********** TEST SET STARTING **************");

	}

	public void testSkipped(ReportEntry arg0) {
		System.out.println("*********** TEST SKIPPED **************");

	}

	public void testStarting(ReportEntry arg0) {
		System.out.println("*********** TEST STARTING **************");

	}

	public void testSucceeded(ReportEntry arg0) {
		System.out.println("*********** TEST SUCCEED **************");

	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		// TODO Auto-generated method stub
		System.out.println("*********** testAssumptionFailure");
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("*********** testFailure");
	}

	@Override
	public void testFinished(Description description) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("*********** testFinished");
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		System.out.println("*********** testIgnored");
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		System.out.println("*********** testRunFinished");
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		System.out.println("*********** testRunStarted");
	}

	@Override
	public void testStarted(Description description) throws Exception {
		System.out.println("*********** testStarted");
	}
	
	

}
