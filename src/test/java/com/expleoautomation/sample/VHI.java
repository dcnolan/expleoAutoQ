package com.expleoautomation.sample;

import org.openqa.selenium.By;

import com.codeborne.selenide.selector.ByShadow;
import com.expleoautomation.commons.ConstantsProvider;
import com.expleoautomation.utils.WebUI;

public class VHI {

	public String windowHandle = null;

	public VhiHomePage login(String uid, String pwd) {
		userId(uid);
		password(pwd);
		return login();
	}

	public VhiHomePage open(String windowHandle) {
		WebUI.switchTab(windowHandle);
		return new VhiHomePage();
	}


	public VhiHomePage open(String uid, String pwd) {
		open();
		userId(uid);
		password(pwd);
		return login();
	}

	public String open() {
		this.windowHandle = WebUI.openUrl(ConstantsProvider.getWEB_URL());	
		WebUI.tryClick(By.cssSelector("#accept-recommended-btn-handler"));
		return this.windowHandle;
	}

	public void userId(String uid) {
		WebUI.set(ByShadow.cssSelector("input[name='userId']", "app-login"), uid);
	}

	public void password(String value) {
		WebUI.set(ByShadow.cssSelector("input[name='password']", "app-login"), value);
	}

	public VhiHomePage login() {
		WebUI.click(ByShadow.cssSelector("#loginbutton", "app-login"));
		return new VhiHomePage();
	}
}
