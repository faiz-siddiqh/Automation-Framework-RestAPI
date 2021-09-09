package stepdefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AddPlace {

	@Given("Add Place Payload")
	public void add_place_payload() {
		System.out.println("Hi");
	}

	@When("User calls {string} API with POST request")
	public void user_calls_api_with_post_request(String string) {
	}

	@Then("Then the response must be sucessful")
	public void then_the_response_must_be_sucessful() {
	}

	@Then("Status Code must be {string}")
	public void status_code_must_be(String string) {
	}

}
