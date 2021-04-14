package oAuth;

import static io.restassured.RestAssured.given;

import java.lang.reflect.Method;

import org.openqa.selenium.WebElement;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import core.BaseUtils;
import core.OAuth_Utils;

public class OAuth2_0 {

	OAuth_Utils oAuth = new OAuth_Utils();

	@BeforeClass
	public void setUp() {
		oAuth.setUp();

	}

	@Test(priority = 0)
	public void getAuthCode(Method method) {
		oAuth.init(method.getName());
		String authUrl = BaseUtils.testData.getTestData("AuthUrl");
		String email = BaseUtils.testData.getTestData("Email");

		BaseUtils.setUpDriver();
		BaseUtils.common.navigateToUrl(authUrl);
		BaseUtils.waitForThePageToLoad();
		BaseUtils.waitToPerformAction(5000, "Enter the email manually");

		WebElement element = BaseUtils.getElement(BaseUtils.locators.getLocator("OAuth-GmailLogin-Email"),
				"cssSelector");
		BaseUtils.clickAndTypeAndWait(element, email, "Entering the email");
		BaseUtils.waitToPerformAction(5000, "Click on next Button ");
		BaseUtils.clickAndWait(
				BaseUtils.getElement(BaseUtils.locators.getLocator("OAuth-GmailLogin-Password"), "cssSelector"),
				"Click on the password field");

		BaseUtils.waitToPerformAction(6000, "Enter the password manually ");

		String getCurrentUrl = BaseUtils.common.getWebDriver().getCurrentUrl();
		String partialUrl = getCurrentUrl.split("code=")[1];
		String authCode = partialUrl.split("&scope")[0];
		oAuth.setAuthCode(authCode);
	}

	@Test(priority = 1)
	public void getAccessToken(Method method) {
		oAuth.init(method.getName());
		String clientId = BaseUtils.testData.getTestData("Client_Id");
		String client_secret = BaseUtils.testData.getTestData("Client_secret");
		String grantType = BaseUtils.testData.getTestData("Grant_Type");
		String redirectUri = BaseUtils.testData.getTestData("Redirect_Uri");
		String resource = BaseUtils.testData.getTestData("Resource");
		/*
		 * urlEncodingEnabled(false) is because restassured will convert the special
		 * characters in the code into respective alpahabets,So in order to prevent it
		 * this method is used
		 */
		String response = given().urlEncodingEnabled(false).queryParam("code", oAuth.getAuthCode())
				.queryParam("client_id", clientId).queryParam("client_secret", client_secret)
				.param("redirect_uri", redirectUri).queryParam("grant_type", grantType).when().post(resource)
				.asString();
		String accessToken = BaseUtils.extractFromJson(response, "access_token");
		oAuth.setAccessToken(accessToken);

	}

	@Test
	public void performOAuthAuthentication(Method method) {
		oAuth.init(method.getName());
		String resource = BaseUtils.testData.getTestData("resource");

		given().queryParam("access_token", oAuth.getAccessToken()).when().get(resource).asString();

	}

	@AfterMethod
	public void cleanUp(ITestResult result) {

		if (result.getStatus() == ITestResult.SUCCESS) {
			BaseUtils.common.cleanUpOnSuccess();
		} else if (result.getStatus() == ITestResult.FAILURE) {
			BaseUtils.common.cleanUpOnFailure();
		}

	}

}
