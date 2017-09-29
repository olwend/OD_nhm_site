@hello-world
Feature: As a user I want to search for query on wikipedia page

	Scenario: Search for 'hello-world' query
		Given I open wikipedia homepage
		When I search for "hello world" query
		Then definition page with header "\"Hello, World!\" program" is displayed