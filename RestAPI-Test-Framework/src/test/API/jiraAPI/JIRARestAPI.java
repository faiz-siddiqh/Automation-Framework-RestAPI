package jiraAPI;

import java.io.File;
import java.lang.reflect.Method;
import static io.restassured.RestAssured.given;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import core.BaseUtils;
import core.Jira_Utils;

public class JIRARestAPI {

	private Jira_Utils jira = new Jira_Utils();

	@BeforeClass
	public void setUp() {
		jira.setUp();
	}

	@Test(priority = 0)
	public void createIssue(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String postResource = BaseUtils.testData.getTestData("postResource");
		String statusCode = BaseUtils.testData.getTestData("statusCode");

		String response = given().header(headerName, headerValue).body(jira.getCreateIssueBody())
				.filter(Jira_Utils.session).when().post(postResource).then().assertThat()
				.statusCode(Integer.parseInt(statusCode)).extract().asString();
		BaseUtils.common.logInfo(response);
		String issueId = BaseUtils.extractFromJson(response, "id");
		jira.setIssueId(issueId);
	}

	@Test(priority = 1)
	public void getIssue(Method method) {
		jira.init(method.getName());

		String getResource = BaseUtils.testData.getTestData("getResource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		// fetching only comments from the response
		String response = given().relaxedHTTPSValidation().pathParam("issueId", jira.getIssueId())
				.queryParam("fields", "comments").filter(Jira_Utils.session).when().get(getResource).then().assertThat()
				.statusCode(statusCode).extract().response().asString();

		BaseUtils.common.logInfo(response);

	}

	@Test
	public void deleteIssue(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String deleteResource = BaseUtils.testData.getTestData("deleteResource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		// fetching only comments from the response
		String response = given().pathParam("id", jira.getIssueId()).queryParam("true").header(headerName, headerValue)
				.filter(Jira_Utils.session).when().delete(deleteResource).then().assertThat().statusCode(statusCode)
				.extract().asString();

		BaseUtils.common.logInfo(response);

	}

	@Test
	public void addAssignee(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String Resource = BaseUtils.testData.getTestData("Resource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));
		given().pathParam("issueId", jira.getIssueId()).header(headerName, headerValue).body(jira.getAddAssigneeBody())
				.filter(Jira_Utils.session).when().put(Resource).then().assertThat().statusCode(statusCode);

	}

	@Test
	public void addComment(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String Resource = BaseUtils.testData.getTestData("Resource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		String response = given().pathParam("issueId", jira.getIssueId()).header(headerName, headerValue)
				.body(jira.getCreateCommentBody()).filter(Jira_Utils.session).when().post(Resource).then().assertThat()
				.statusCode(statusCode).extract().asString();

		String commentId = BaseUtils.extractFromJson(response, "id");

		jira.setCommentId(commentId);

	}

	@Test
	public void updateComment(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String Resource = BaseUtils.testData.getTestData("Resource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		given().pathParam("issueId", jira.getIssueId()).pathParam("commentId", jira.getCommentId())
				.header(headerName, headerValue).body(jira.getUpdateCommentBody()).filter(Jira_Utils.session).when()
				.put(Resource).then().assertThat().statusCode(statusCode);

	}

	@Test
	public void deleteComment(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value");
		String Resource = BaseUtils.testData.getTestData("Resource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		given().pathParam("issueId", jira.getIssueId()).pathParam("commentId", jira.getCommentId())
				.header(headerName, headerValue).filter(Jira_Utils.session).when().delete(Resource).then().assertThat()
				.statusCode(statusCode);

	}

	@Test(enabled = false)
	public void addAttachments(Method method) {
		jira.init(method.getName());
		String headerName = BaseUtils.locators.getLocator("Header-Name");
		String headerValue = BaseUtils.locators.getLocator("Header-Value-Attachments");
		String Resource = BaseUtils.testData.getTestData("Resource");
		int statusCode = Integer.parseInt(BaseUtils.testData.getTestData("statusCode"));

		String attachmentHeader = BaseUtils.testData.getTestData("headerName");
		String attachmentHeaderValue = BaseUtils.testData.getTestData("headerValue");

		given().pathParam("issueId", jira.getIssueId()).filter(Jira_Utils.session).header(headerName, headerValue)
				.header(attachmentHeader, attachmentHeaderValue).multiPart("file", new File("exercise.json")).when()
				.post(Resource).then().assertThat().statusCode(statusCode);
	}

}
