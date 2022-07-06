#Author: david.nolan@expleogroup.com
@Samples
Feature: Sample test cases
  Sample tests
  


		
@Items @Api
Scenario: Get employee list via API
     Given I can get a list of employees
     
     
     
     
     
@Api @DataIteration
Scenario Outline: Create Employee with (param)
    Given I can create an employee with "<name>", "<age>", "<salary>"
   	Then  I can match these parameters with a record in the database		
Examples: 
    | name                | age   | salary       |	
  	| Fred Flintstone     | 25    | $ 25,000     |
  	| Barny Rubble        | 37    | $ 57,000     |
  	
  		


@Api @Excel
Scenario: Create Employee with (excel)
    Given I can create an employee with "employee-1"
   	Then  I can match these parameters with a record in the database		
  	
  	
  	

@Api @DataTable_verticle
Scenario: Create Employee with (datatable)
    Given I can create an employee with
	    | employee_name     | Fred Flintstone   |
	    | employee_age      | 25                |
	    | employee_salary   | $ 25,000          |	
    Then  I can match these parameters with a record in the database		



  	
  	
  	
@Web
Scenario: Open a sample WEB page
     Given I have access to VHI
     When  I can request a quote for travel insurance
     Then  I am quoted for premium of "€ 54.00"
         
    
@Database
Scenario: Database Query
    Given I can query the database																									 

  				    
@CsvFile @File
Scenario: File serlization
		Given I create a CSV file
		

