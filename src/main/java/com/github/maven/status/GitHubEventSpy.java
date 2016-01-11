package com.github.maven.status;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.BuildFailure;
import org.apache.maven.execution.BuildSuccess;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * @author alain
 *
 */
@Component( role = EventSpy.class, hint = "github-event-spy" )
public class GitHubEventSpy extends AbstractEventSpy {

	@Requirement
	private Logger logger;

	private Context context;
	
	private Map<String, Integer> classNames = new HashMap<String, Integer>();
	
	@Override
	public void close() throws Exception {
		super.close();
		
		// Learning about the events we have captured
		logger.info("***************");
		for(String className : classNames.keySet()) {
			logger.info(className + " : " + classNames.get(className));
		}
		logger.info("***************");
	}
	
	public Context getContext() {
		return context;
	}

	@Override
	public void init(Context context) throws Exception {
		super.init(context);
		this.context = context;
		logger.info("GitHub Event Spy succesfully initialized");
	}

	@Override
	public void onEvent(Object event) throws Exception {
		// Capturing the various event types we receive so we can tackle one after the other
		// This code should be later removed
		if(event != null) {
			Integer currentCount = classNames.get(event.getClass().getName());
			if(currentCount == null) {
				currentCount = new Integer(1); 
			} else {
				currentCount = new Integer(currentCount.intValue()+1);
			}
			
			classNames.put( event.getClass().getName(), currentCount);
		}
		
		if(event != null) {
			if(event instanceof org.apache.maven.execution.DefaultMavenExecutionResult) {
				processExecutionResult((DefaultMavenExecutionResult)event);
			} else if(event instanceof org.apache.maven.execution.ExecutionEvent) {
				processExecutionEvent((ExecutionEvent)event);
			}
		}
		
	} 
	
	void processExecutionResult(DefaultMavenExecutionResult executionResult) {
		BuildSummary bs = executionResult.getBuildSummary(executionResult.getProject());
		if(bs instanceof BuildSuccess) {
			logger.info("BUILD SUCCESS for " + bs.getProject().getName());
		} else if(bs instanceof BuildFailure) {
			logger.info("BUILD FAILURE for " + bs.getProject().getName());
		} else {
			logger.info("WHAT'S THE HECK with " + bs.getProject().getName());
		}
	}
	
	void processExecutionEvent(ExecutionEvent executionEvent) {
		//logger.info("OOOOOOOHHHHHHHH YYYYYYYYYIIIIIIIIIISSSSSSSSSSS : " + executionEvent.getType());
		
		switch(executionEvent.getType()) {
			case MojoSucceeded : 
				logger.info("******");
				logger.info("Group Id : " + executionEvent.getMojoExecution().getGroupId());
				logger.info("Artifact Id : " + executionEvent.getMojoExecution().getArtifactId());
				logger.info("Goal : " + executionEvent.getMojoExecution().getGoal());
				break;
			case MojoFailed : 
				logger.info(executionEvent.getMojoExecution().toString());
				break;
			case ProjectSucceeded : 
				logger.info("*************** PROJECT SUCCEEDED ********************");
				break;
			case ProjectFailed : 
				logger.info("*************** PROJECT FAILED ********************");
				break;
			default : 
				break; 
		}
	}

}
