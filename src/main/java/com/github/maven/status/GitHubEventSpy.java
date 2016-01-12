package com.github.maven.status;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

	private Map<String, Integer> classNames = new HashMap<String, Integer>();

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

		String githubEndpointPrefix = "https://api.github.com/";

		String githubRepo;

		String sha;

		Properties userProperties = (Properties) context.getData().get("userProperties");
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
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws Exception {
		super.close();

		// Displaying the events
		logger.info(" ** Events received by m2github ** ");
		for (String className : classNames.keySet()) {
			logger.info(" ** " + className + " : " + classNames.get(className));
		}
	}

	@Override
	public void onEvent(Object event) throws Exception {
		// Capturing the various event types we receive so we can report on
		// those being ignored
		if (event != null) {
			Integer currentCount = classNames.get(event.getClass().getName());
			if (currentCount == null) {
				currentCount = new Integer(1);
			} else {
				currentCount = new Integer(currentCount.intValue() + 1);
			}

			classNames.put(event.getClass().getName(), currentCount);
		}

		if (event != null) {
			if (event instanceof org.apache.maven.execution.DefaultMavenExecutionResult) {
				processExecutionResult((DefaultMavenExecutionResult) event);
			} else if (event instanceof org.apache.maven.execution.ExecutionEvent) {
				processExecutionEvent((ExecutionEvent) event);
			}
		}

	}

	protected void processExecutionResult(DefaultMavenExecutionResult executionResult) {
		BuildSummary bs = executionResult.getBuildSummary(executionResult.getProject());

		if (bs instanceof BuildSuccess) {
			sendStatus(bs.getProject().getName(), GitHubStatus.Success);
		} else if (bs instanceof BuildFailure) {
			sendStatus(bs.getProject().getName(), GitHubStatus.Failure);
		} else {
			logger.error("m2github - unknown status for " + bs.getProject().getName() + " - " + bs.getClass().getName());
		}
	}

	protected void processExecutionEvent(ExecutionEvent executionEvent) {
		switch (executionEvent.getType()) {
		case MojoStarted:
			sendStatus(generateMojoName(executionEvent), GitHubStatus.Pending);
			break;
		case MojoSucceeded:
			sendStatus(generateMojoName(executionEvent), GitHubStatus.Success);
			break;
		case MojoFailed:
			sendStatus(generateMojoName(executionEvent), GitHubStatus.Failure);
			break;
		case ProjectStarted:
			sendStatus(generateProjectName(executionEvent), GitHubStatus.Pending);
			break;
		case ProjectSucceeded:
			sendStatus(generateProjectName(executionEvent), GitHubStatus.Success);
			break;
		case ProjectFailed:
			sendStatus(generateProjectName(executionEvent), GitHubStatus.Failure);
			break;
		default:
			break;
		}
	}

	protected void sendStatus(String label, GitHubStatus status) {
		logger.info("  ** " + label + " : " + status + "  ** ");

		HttpPost httpPostRequest = new HttpPost(githubEndpoint);

		try {
			String payload = String.format(
					"{\"state\": \"%s\", \"target_url\": \"%s\", \"description\": \"%s\", \"context\": \"%s\"}",
					status, "http://github.com", "This is a meaningful description", label);

			StringEntity params = new StringEntity(payload);

			httpPostRequest.addHeader("content-type", "application/json");
			httpPostRequest.addHeader("Authorization", "token " + githubToken);

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
			httpPostRequest.setConfig(requestConfig);

			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);
			if(response.getStatusLine().getStatusCode() >= 300) {
				logger.error(response.getStatusLine().toString());
			}
			logger.info(response.getStatusLine().toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			httpPostRequest.releaseConnection();
		}

		// post.s

	}

	private String generateMojoName(ExecutionEvent executionEvent) {
		return String.format("%s/%s/%s/%s", executionEvent.getProject().getName(), executionEvent.getMojoExecution()
				.getGroupId(), executionEvent.getMojoExecution().getArtifactId(), executionEvent.getMojoExecution()
				.getGoal());
	}

	private String generateProjectName(ExecutionEvent executionEvent) {
		return executionEvent.getProject().getName();
	}

}
