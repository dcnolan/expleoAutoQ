package com.expleoautomation.sample;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.expleoautomation.utils.DatabaseUtils;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Steps {

	// property bag to persist state between steps
	public static Map<String, String> browserTabs = new HashMap<String, String>();
	public static Map<String, String> properties = new HashMap<String, String>();

	

	@Given("I have access to VHI")
	public void I_have_access_to_VHI() {
		
		// open web URL
		VHI vhi = new VHI();
		String windowHandle = vhi.open();
		browserTabs.put("", windowHandle);
	}
	
	
	
	
	@When("I can request a quote for travel insurance")
	public void I_can_request_a_quote_for_travel_insurance() {
		// navigate URL
		VhiHomePage home = new VhiHomePage();
		VhiTravelPage travel = home.Travel();
		travel.StartDate(LocalDate.now().plusDays(7));
		travel.HasHealthInsurance(false);
		travel.NumberOfPeople(1);
		travel.Age(25);
		VhiTravelQuote quote = travel.GetQuote();
		
		// pass variable between steps
		properties.put("Price", quote.Price());
	}
	@Then("I am quoted for premium of {string}")
	public void I_am_quoted_for_premium_of(String expectedPrice) {
		
		// retrieve variable from previous step
		String actualPrice = properties.get("Price");
		
		// assert
		Assert.assertEquals(expectedPrice, actualPrice);
	}
    
    
    

	
	@When("I can get a list of employees")
	public void i_can_get_a_list_of_employees() {

		// using API object model
		Employees employees = new Employees();
		employees.get();

		

	}


	@When("I can create an employee with {string}, {string}, {string}")
	public void I_can_create_an_employee_with(String name, String age, String salary) {
		
		Employee employee = new Employee();
		employee.employee_name = name;
		employee.employee_age = age;
		employee.employee_salary = salary;
		employee.post();
		

		properties.put("Employee", employee.toJson()); 
		properties.put("SQL", employee.getSelectSQL()); 
	}

	
	@When("I can create an employee with")
	public void I_create_an_employee_with(io.cucumber.datatable.DataTable dataTable) {

		
		Employee employee = new Employee();
		employee.fromDataTable(dataTable);
		employee.post();
		
		properties.put("Employee", employee.toJson()); 
		properties.put("SQL", employee.getSelectSQL()); 
	}
	
	
	
	@When("I can create an employee with {string}")
	public void I_can_create_an_employee_with(String xlsRef) {
		

		Employee employee = new Employee();
		employee.fromXls(xlsRef);
		employee.post();

		properties.put("Employee", employee.toJson()); 
		properties.put("SQL", employee.getSelectSQL());
	}

	
	
	@And("I create a CSV file")
	public void I_create_a_CSV_file() {
		
		// generate GLFEED file
		CsvFile myFile = new CsvFile("blah", "123.45");
		String filePath = myFile.toTempFile("CSV");
	}
	

	@And("I can match these parameters with a record in the database")
	public void I_can_match_these_parameters_with_a_record_in_the_database() {
//		DatabaseUtils.findMatchingRecords(properties.get("SQL"), 1);
	}
	


	@Given("I can query the database")
	public void i_can_query_the_database() {
	    String sql = "SELECT * FROM [Sheet1$]";
	    DatabaseUtils.select(sql, true);
	}

}
