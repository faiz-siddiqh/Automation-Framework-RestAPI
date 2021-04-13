package core;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.filter.session.SessionFilter;

public class Jira_Utils {

	public static SessionFilter session = new SessionFilter();
	private String issueId;
	private String commentId;

	public void setUp() {
		BaseUtils.setUp("JiraAPI");
		createSession();
	}

	public void init(String methodName) {
		BaseUtils.common.setExtentTest(methodName);

	}

	public void createSession() {
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
				.filter(session).when().post(postResource).then().assertThat().statusCode(Integer.parseInt(statusCode));
		BaseUtils.common.logInfo("Session successfully created");
	}

	public static String getJiraCreateSessionBody() {
		String userName = BaseUtils.testData.getGlobalVariablesTestdata("Username");
		String password = BaseUtils.testData.getGlobalVariablesTestdata("Password");

		return "{ \r\n" + "    \"username\": \"" + userName + "\",\r\n" + "    \"password\": \"" + password + "\" \r\n"
				+ "}";
	}

	public String getCreateIssueBody() {

		String projectKey = BaseUtils.testData.getTestData("Key");
		String summary = BaseUtils.testData.getTestData("summary");
		String description = BaseUtils.testData.getTestData("description");
		String issueType = BaseUtils.testData.getTestData("issue-Type");

		return "{\r\n" + "    \"fields\": {\r\n" + "        \"project\": {\r\n" + "            \"key\": \"" + projectKey
				+ "\"\r\n" + "        },\r\n" + "        \"summary\": \"" + summary + "\",\r\n"
				+ "        \"description\": \"" + description + "\",\r\n" + "        \"issuetype\": {\r\n"
				+ "         \"name\": \"" + issueType + "\"\r\n" + "        }\r\n" + "    }   \r\n" + "}";
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;

	}

	public String getIssueId() {
		return issueId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCommentId() {
		return commentId;
	}

	public String getAddAssigneeBody() {
		String assignee = BaseUtils.testData.getTestData("Assignee");

		return "{\r\n" + "    \"name\": \"" + assignee + "\"\r\n" + "}";
	}

	public String getCreateCommentBody() {
		String comment = BaseUtils.testData.getTestData("comment");

		return "{\r\n" + "    \"body\": \"" + comment + "\",\r\n" + "    \"visibility\": {\r\n"
				+ "        \"type\": \"role\",\r\n" + "        \"value\": \"Administrators\"\r\n" + "    }\r\n" + "}";
	}

	public String getUpdateCommentBody() {
		String updatedComment = BaseUtils.testData.getTestData("comment");
		
		return "{\r\n"
				+ "    \"body\": \"\"" + updatedComment + "\"\",\r\n"
				+ "    \"visibility\": {\r\n"
				+ "        \"type\": \"role\",\r\n"
				+ "        \"value\": \"Administrators\"\r\n"
				+ "    }\r\n"
				+ "}";
	}

}
