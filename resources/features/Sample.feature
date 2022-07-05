#Author: david.nolan@expleogroup.com
@Samples
Feature: Sample test cases
  Sample tests
  


				    
@CsvFile @File
Scenario: File serlization
		Given I create a CSV file
		

		
@Items @Api
Scenario: Get ITEMS from simplekart
     Given I have access to SimpleKart 				
     When  I can get items from SimpleKart
     
     
@Orders @Api @Excel
Scenario: Place ORDER with simpleKart (excel)
    Given I have access to SimpleKart 				
    When  I can place an order with SimpleKart for "item-1"
   	Then  I can match these parameters with a record in the database		

@Orders @Api @DataIteration
Scenario Outline: Place ORDER with simpleKart (param)
    Given I have access to SimpleKart 				
    When  I can place an order with SimpleKart for "<itemId>", "<name>", "<price>", "<description>"
   	Then  I can match these parameters with a record in the database		
Examples: 
    | itemId     | name                | price                  | description              |	
  	| 01         | Sausage             | €0.89                  | BBQ time                 |
  	| 02         | Flippers            | €19.99                 | Summer time              |

@Orders @Api @DataTable
Scenario: Place ORDER with simpleKart (datatable)
    Given I have access to SimpleKart 				
    When  I can place an order with SimpleKart for
    Then  I can match these parameters with a record in the database		
    | itemId     | name                | price                  | description              |	
  	| 03         | Slippers            | €7.45                  | Bed time                 |




@Web
Scenario: Open a sample WEB page
     Given I have access to VHI
     When  I can request a quote for travel insurance
     Then  I am quoted for premium of "€ 45.00"
     
     



@Database
Scenario: Database Query
    Given I can query the database																									 
																											 
      
@Post @Delete
Scenario: Authenticate, post & delete
    Given I have access to Multifonds 																															 
    When  I create a default ExchangeGroup 
    And   Delete ExchangeGroup record		
    
@Verify
Scenario: Authenticate, post, delete & verify
    Given I have access to Multifonds																				 
    When  I create a default ExchangeGroup 
    Then  I can match these parameters with a record in the database																										 
    And   Delete ExchangeGroup record		
        

    

@4-eyesLogin @Web
Scenario: web login (4-eyes)
    Given I switch user account for four-eyes validation
    Then  I will close all screens    


    

