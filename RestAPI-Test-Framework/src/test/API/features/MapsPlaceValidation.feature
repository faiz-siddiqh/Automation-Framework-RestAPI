Feature: Validating place API's

Scenario: Verify if Place is added successfully using Add Place API
	Given Add Place Payload
	When User calls "AddPlace" API with POST request
	Then Then the response must be sucessful
	And Status Code must be "OK"
	

