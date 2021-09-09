package core;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.filter.session.SessionFilter;

public class Maps_Utils {

	public static SessionFilter session = new SessionFilter();

	public void setUp() {
		BaseUtils.setUp("Maps");
		createSession();
	}

	public void createSession() {
		try {
			BaseUtils.common.setExtentTest("Create Jira Session");

			String baseUri = BaseUtils.ProjectProperties.readProjectVariables("BaseURI");
			// String baseUri = BaseUtils.testData.getGlobalVariablesTestdata("BaseURI");
			String statusCode = BaseUtils.ProjectProperties.readProjectVariables("sessionStatusCode");
			// String
			// statusCode=BaseUtils.locators.getLocator("Jira-CreateSession-StatusCode");

			String headerName = BaseUtils.locators.getLocator("Header-Name");
			String headerValue = BaseUtils.locators.getLocator("Header-Value");
			String postResource = BaseUtils.locators.getLocator("Jira-CreateSession-post");
			BaseUtils.common.logInfo("Creating a session");
			RestAssured.baseURI = baseUri;
			// relaxedHTTPSValidation() --validating that the site is https
			given().relaxedHTTPSValidation().header(headerName, headerValue).body(getJiraCreateSessionBody())
					.filter(session).when().post(postResource).then().assertThat()
					.statusCode(Integer.parseInt(statusCode));
			BaseUtils.common.logInfo("Session successfully created");

		} catch (Exception e) {
			BaseUtils.common.logInfo("Session initiation failed");
			BaseUtils.common.cleanUpOnFailure();
		} finally {
			BaseUtils.common.cleanUpOnSuccess();
		}

	}

	public static String getJiraCreateSessionBody() {
		String userName = BaseUtils.testData.getGlobalVariablesTestdata("Username");
		String password = BaseUtils.testData.getGlobalVariablesTestdata("Password");
		BaseUtils.common.logInfo("fetching body of Payload");
		return "{ \r\n" + "    \"username\": \"" + userName + "\",\r\n" + "    \"password\": \"" + password + "\" \r\n"
				+ "}";
	}

	public String getCreateIssueBody() {

		String projectKey = BaseUtils.testData.getTestData("Key");
		String summary = BaseUtils.testData.getTestData("summary");
		String description = BaseUtils.testData.getTestData("description");
		String issueType = BaseUtils.testData.getTestData("issue-Type");
		BaseUtils.common.logInfo("fetching body of Payload");
		return "{\r\n" + "    \"fields\": {\r\n" + "        \"project\": {\r\n" + "            \"key\": \"" + projectKey
				+ "\"\r\n" + "        },\r\n" + "        \"summary\": \"" + summary + "\",\r\n"
				+ "        \"description\": \"" + description + "\",\r\n" + "        \"issuetype\": {\r\n"
				+ "         \"name\": \"" + issueType + "\"\r\n" + "        }\r\n" + "    }   \r\n" + "}";
	}

}
