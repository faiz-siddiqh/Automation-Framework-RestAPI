Feature: Validating place API's 
Scenario Outline: Verify if Place is added successfully using Add Place API 
	Given Add Place Payload with "<name>" "<language>" "<address>" 
	When User calls "AddPlace" API with "POST" request 
	Then Then the response must be sucessful 
	And Status Code must be "200" 
	
	Examples: 
		|name |language|address|
		|Rajiv|Hindi|Chinchpokli Bandar|
		|Kapoor|English|Calcutta|
		
		
