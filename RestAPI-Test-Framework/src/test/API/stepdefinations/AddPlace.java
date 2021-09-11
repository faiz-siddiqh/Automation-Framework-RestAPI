package stepdefinations;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import core.ApiUtils;
import core.Maps_Utils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import payloads.Maps_Payload;
import resources.APIResources;

public class AddPlace {

	Maps_Utils utils = new Maps_Utils();
	Maps_Payload payload = new Maps_Payload();
	RequestSpecification reqSpec;
	ResponseSpecification resSpec;
	Response res;

	@Before
	public void setUp() {
		utils.setUp();
	}

	@Given("Add Place Payload with {string} {string} {string}")
	public void add_place_payload(String name, String language, String place) {

		reqSpec = given().spec(utils.getRequestSpec()).body(payload.addPlacePayLoad(name, language, place));

	}

	@When("User calls {string} API with {string} request")
	public void user_calls_api_with_post_request(String resourceType, String HTTPMethod) {
		APIResources resource = APIResources.valueOf(resourceType);
		if (HTTPMethod.equalsIgnoreCase("POST"))
			res = reqSpec.when().post(resource.getResource());
	}

	@Then("Then the response must be sucessful")
	public void then_the_response_must_be_sucessful() {
		res.then().assertThat().spec(utils.getResponseSpec());
	}

	@Then("Status Code must be {string}")
	public void status_code_must_be(String string) {
		assertEquals(res.getStatusCode(), Integer.parseInt(string));
	}

	@After
	public void tearDown() {
		ApiUtils.common.cleanUpOnSuccess();
	}
}
