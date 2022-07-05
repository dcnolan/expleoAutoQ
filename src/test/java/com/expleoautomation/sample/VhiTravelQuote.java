package com.expleoautomation.sample;

import java.time.LocalDate;

import org.openqa.selenium.By;

import com.expleoautomation.utils.WebUI;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class VhiTravelQuote {
	
	public VhiTravelQuote() {
		WebUI.waitForUrlChange("quote");
	}

	public String Price() {
		String price = WebUI.get(By.cssSelector("#price"));
		return price;
	}
}
