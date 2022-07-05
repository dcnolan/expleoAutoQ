package com.expleoautomation.sample;

import com.expleoautomation.utils.WebUI;

public class VhiHealthPage {
	
	public VhiHealthPage() {
		WebUI.waitForUrlChange("health-insurance");
	}

}
