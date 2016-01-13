package com.github.maven.status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * @author Alain Helaili - helaili@github.com
 *
 */
@Component(role = EventSpy.class, hint = "github-event-spy")
public class GitHubEventSpy extends AbstractEventSpy {
	@Requirement
	private Logger logger;

	private Context context;

	private HttpClient httpClient;

	private Map<String, Integer> eventClassNames = new HashMap<String, Integer>();
	
	private Set<String> executionEventReceived = new HashSet<String>();
	
	private SpyConfig spyConfig;

	private boolean initError = false;

	private String githubToken;

	private String githubEndpoint;

	public enum GitHubStatus {
		Pending("pending"), Success("success"), Failure("failure"), Error("error");

		private final String statusLabel;

		/**
		 * @param statusLabel
		 */
		private GitHubStatus(final String statusLabel) {
			this.statusLabel = statusLabel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return statusLabel;
		}
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void init(Context context) throws Exception {
		super.init(context);
		this.context = context;

		String configFile = "m2github.json";
		
		String githubEndpointPrefix = "https://api.github.com/";

		String githubRepo;

		String sha;
		
		
		Properties userProperties = (Properties) context.getData().get("userProperties");
		
		if (userProperties.getProperty("m2github.configFile") != null) {
			configFile = userProperties.getProperty("m2github.configFile");
		}
		try {
			FileReader fileReader = new FileReader(configFile);
			logger.info(" ** m2github - Using config file " + configFile);
			JsonReader reader = new JsonReader(fileReader);
	        Gson gson = new GsonBuilder().create();
	        
            spyConfig = gson.fromJson(reader, SpyConfig.class);
            
		} catch (Exception e1) {
			logger.info(" ** m2github - No config file found");
		}
		
		githubRepo = userProperties.getProperty("m2github.repo");
		if (githubRepo == null) {
			logger.error("m2github - Missing property m2github.repo");
			initError = true;
		}

		githubToken = userProperties.getProperty("m2github.token");
		if (githubToken == null) {
			logger.error("m2github - Missing property m2github.token");
			initError = true;
		}

		if (userProperties.getProperty("m2github.endpoint") != null) {
			githubEndpointPrefix = userProperties.getProperty("m2github.endpoint");
		}

		// Need the current SHA
		Process gitProcess = Runtime.getRuntime().exec("git rev-parse HEAD");
		gitProcess.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
		StringBuffer shaStringBuffer = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			shaStringBuffer.append(line);
		}
		sha = shaStringBuffer.toString();

		if (sha == null) {
			logger.error("m2github - Couldn't figure out SHA1");
			initError = true;
		}

		if (!initError) {
			try {
				githubEndpoint = githubEndpointPrefix + "/repos/" + githubRepo + "/statuses/" + sha;

				httpClient = HttpClients.createDefault();

				if (httpClient == null) {
					logger.error(" ** m2github - Failed to initialize HTTP Client");
					System.exit(1);
				}

				logger.info(" ** m2github - GitHub Event Spy succesfully initialized - Endpoint is " + githubEndpoint
						+ " ** ");
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws Exception {
		super.close();

		/*
		// Displaying the event classes
		logger.info(" ** Event Classes received by m2github ** ");
		for (String className : eventClassNames.keySet()) {
			logger.info(" ** " + className + " : " + eventClassNames.get(className));
		}
		*/
		
		logger.info(" ** Execution events received by m2github ** ");
		
		// Displaying execution events (class = org.apache.maven.execution.ExecutionEvent)
		for(String executionEvent : executionEventReceived) {
			logger.info(" ** " + executionEvent );
		}
		
	}

	@Override
	public void onEvent(Object event) throws Exception {
		// Capturing the various event types we receive so we can report on
		// those being ignored
		if (event != null) {
			Integer currentCount = eventClassNames.get(event.getClass().getName());
			if (currentCount == null) {
				currentCount = new Integer(1);
			} else {
				currentCount = new Integer(currentCount.intValue() + 1);
			}

			eventClassNames.put(event.getClass().getName(), currentCount);
		}

		if (event != null && !initError) {
			if (event instanceof org.apache.maven.execution.DefaultMavenExecutionResult) {
				processExecutionResult((DefaultMavenExecutionResult) event);
			} else if (event instanceof org.apache.maven.execution.ExecutionEvent) {
				processExecutionEvent((ExecutionEvent) event);
			}
		}

	}

	protected void processExecutionResult(DefaultMavenExecutionResult executionResult) {
		BuildSummary bs = executionResult.getBuildSummary(executionResult.getProject());
		
		String statusLabel = generateBuildSummaryName(bs); 
		executionEventReceived.add(statusLabel);

		if (bs instanceof BuildSuccess) {
			sendStatus(statusLabel, "Duration : " + bs.getTime() + "ms", GitHubStatus.Success);
		} else if (bs instanceof BuildFailure) {
			sendStatus(statusLabel, "Duration : " + bs.getTime() + "ms", GitHubStatus.Failure);
		} else {
			logger.error("m2github - unknown status for " + statusLabel + " - " + bs.getClass().getName());
		}
	}

	protected void processExecutionEvent(ExecutionEvent executionEvent) {
		String statusLabel = null;
		GitHubStatus statusType = null;
		
		
		switch (executionEvent.getType()) {
		case MojoStarted:
			statusLabel = generateMojoName(executionEvent);
			statusType = GitHubStatus.Pending;
			break;
		case MojoSucceeded:
			statusLabel = generateMojoName(executionEvent);
			statusType = GitHubStatus.Success;
			break;
		case MojoFailed:
			statusLabel = generateMojoName(executionEvent);
			statusType = GitHubStatus.Failure;
			break;
		case ProjectStarted:
			statusLabel = generateProjectName(executionEvent);
			statusType = GitHubStatus.Pending;
			break;
		case ProjectSucceeded:
			statusLabel = generateProjectName(executionEvent);
			statusType = GitHubStatus.Success;
			break;
		case ProjectFailed:
			statusLabel = generateProjectName(executionEvent);
			statusType = GitHubStatus.Failure;
			break;
		default:
			break;
		}
		if(statusLabel != null && statusType != null) {
			executionEventReceived.add(statusLabel);
			String message;
			
			if(statusType == GitHubStatus.Pending) {
				message = "Just Started";
			} else if(statusType == GitHubStatus.Failure) {
				message = "Ouch, Failure";
			} else if(statusType == GitHubStatus.Success) {
				message = "Oh, Sweet Success!";
			} else {
				message = "You should never really get this message. Please call me if you do.";
			}
			sendStatus(statusLabel, message, statusType);
		}
	}

	/**
	 * Do the actual sending of the status to GitHub
	 * 
	 * @param label
	 *            The label of the status we want to send
	 * @param message
	 *            The message displayed on the status page
	 * @param status
	 *            The status (success | failure | pending)
	 */
	protected void sendStatus(String label, String message, GitHubStatus status) {
		HttpPost httpPostRequest = new HttpPost(githubEndpoint);

		try {
			String payload = String.format(
					"{\"state\": \"%s\", \"target_url\": \"%s\", \"description\": \"%s\", \"context\": \"%s\"}",
					status, "http://github.com", message, label);
			
			logger.debug(payload);

			StringEntity params = new StringEntity(payload);

			httpPostRequest.addHeader("content-type", "application/json");
			httpPostRequest.addHeader("Authorization", "token " + githubToken);

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
			httpPostRequest.setConfig(requestConfig);

			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);
			if (response.getStatusLine().getStatusCode() >= 300) {
				logger.error(response.getStatusLine().toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			httpPostRequest.releaseConnection();
		}
	}

	/**
	 * Generates a label for a Mojo related event
	 * 
	 * @param executionEvent
	 * @return the concatenation of the project, the mojo's group id, the mojo's
	 *         artifact id and the goal
	 */
	private String generateMojoName(ExecutionEvent executionEvent) {
		String mojoName =  String.format("%s/%s/%s/%s", executionEvent.getProject().getName(), executionEvent.getMojoExecution()
				.getGroupId(), executionEvent.getMojoExecution().getArtifactId(), executionEvent.getMojoExecution()
				.getGoal());
		
		if(spyConfig != null) {
			if(spyConfig.isIgnored(mojoName)) {
				return null;
			} else {
				String mapping = spyConfig.getMapping(mojoName);
				return mapping == null ? mojoName : mapping;
			}
		} else {
			return mojoName;
		}
	}

	private String generateProjectName(ExecutionEvent executionEvent) {
		String projectName = executionEvent.getProject().getName();
		
		if(spyConfig != null) {
			if(spyConfig.isIgnored(projectName)) {
				return null;
			} else {
				String mapping = spyConfig.getMapping(projectName);
				return mapping == null ? projectName : mapping;
			}
		} else {
			return projectName;
		}
	}
	
	private String generateBuildSummaryName(BuildSummary bs) {
		String bsName = bs.getProject().getName();
		
		if(spyConfig != null) {
			if(spyConfig.isIgnored(bsName)) {
				return null;
			} else {
				String mapping = spyConfig.getMapping(bsName);
				return mapping == null ? bsName : mapping;
			}
		} else {
			return bsName;
		}
	}

}
