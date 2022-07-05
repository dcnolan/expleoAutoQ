package com.expleoautomation.sample;

import java.time.LocalDate;

import org.openqa.selenium.By;

import com.expleoautomation.utils.WebUI;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class VhiTravelQuote {
	
	public VhiTravelQuote() {
		log.debug("VhiTravelQuote() b4 waitForUrlChange");
		WebUI.waitForUrlChange("quote");
		log.debug("VhiTravelQuote() after waitForUrlChange");
	}

	public String Price() {
		log.debug("VhiTravelQuote() b4 Price");
		String price = WebUI.get(By.cssSelector("#price"));
		log.debug("VhiTravelQuote() afyter Price " + price);
		return price;
	}
}
