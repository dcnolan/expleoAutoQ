package com.expleoautomation.sample;

import org.openqa.selenium.By;

import com.expleoautomation.utils.WebUI;

public class VhiHomePage {
	
	
	public VhiHealthPage Health() {
		WebUI.click(By.cssSelector("#navItemId0"));
		return new VhiHealthPage();
	}
	public VhiTravelPage Travel() {
		WebUI.click(By.cssSelector("#navItemId1"));
		return new VhiTravelPage();
	}
	public VhiDentalPage Dental() {
		WebUI.click(By.cssSelector("#navItemId2"));
		return new VhiDentalPage();
	}
	public VhiLifePage Life() {
		WebUI.click(By.cssSelector("#navItemId3"));
		return new VhiLifePage();
	}
	


}
