package com.expleoautomation.sample;

import java.time.LocalDate;


import org.junit.Assert;
import org.openqa.selenium.By;

import com.expleoautomation.utils.WebUI;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class VhiTravelPage {
	
	public VhiTravelPage() {
		WebUI.waitForUrlChange("travel-insurance");
	}

	public void StartDate(LocalDate value) {
		String stringValue = String.format("%s/%s/%s", value.getDayOfMonth(), value.getMonthValue(), value.getYear());
		WebUI.set(By.cssSelector("#quotePolicyStart"), stringValue);
	}

	
	public void HasHealthInsurance(boolean hasHealthInsurance) {
		if (hasHealthInsurance) { 
			HasHealthInsurance_Yes();
		} else {
			HasHealthInsurance_No();
		}
	}
	public void HasHealthInsurance_Yes() {
		WebUI.click(By.cssSelector("#yes-button"));
	}
	public void HasHealthInsurance_No() {
		WebUI.click(By.cssSelector("#no-button"));
	}
	
	
	
	public void NumberOfPeople(int numberOfPeople) {
		switch (numberOfPeople) { 
			case 1: OnePerson(); break;
			case 2: TwoPeople(); break;
			case 3: ThreePeople(); break;
			default: Assert.fail("NumberOfPeople(): Invalid value (" + numberOfPeople + ")");
		}
	}
	public void OnePerson() {
		WebUI.click(By.cssSelector("#icon-one-person"));
	}
	public void TwoPeople() {
		WebUI.click(By.cssSelector("#icon-two-person"));
	}
	public void ThreePeople() {
		WebUI.click(By.cssSelector("#icon-three-person"));
	}

	
	public void Age(int age) {
		if (age < 65) { 
			AgeUnder65();
		} else {
			AgeOver65();
		}
	}
	public void AgeUnder65() {
		WebUI.click(By.cssSelector("#under-sixty-five-margin"));
	}
	public void AgeOver65() {
		WebUI.click(By.cssSelector("#quoteAgeRangeOVER_65"));
	}

	public VhiTravelQuote GetQuote() {
		log.debug("GetQuote() b4 ");
		WebUI.click(By.cssSelector("#getCovered"));
		log.debug("GetQuote() after ");
		VhiTravelQuote v = new VhiTravelQuote();
		log.debug("GetQuote() return ");
		return v;
	}


}
