package com.expleoautomation.utils;

import static com.codeborne.selenide.Selenide.*;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.expleoautomation.commons.Behaviour;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.TimeoutException;
import com.expleoautomation.commons.ConstantsProvider;
import com.expleoautomation.sample.Steps;

import lombok.extern.log4j.Log4j2;
import static com.codeborne.selenide.Condition.*;


@Log4j2
public class WebUI {

	public static Map<String, Integer> tableCache = new HashMap<String, Integer>();
	public static boolean initialised = false;
	
	public static void initialise() {
		if (!initialised) {
			setBrowser("edge");
			if (!Configuration.headless) {
				killUnwantedProcess();
			}
			initialised=true;
		}
	}

	private static void setBrowser(String browser) {
		switch (browser) {
			case "chrome":	System.setProperty("webdriver.chrome.driver", "resources\\drivers\\chromeDriver.exe.100");	break;
			case "edge":	System.setProperty("webdriver.edge.driver", "resources\\drivers\\msEdgeDriver.exe");		break;
		}
		Configuration.browser = browser;
	}

	private static void configure() {
		if (Behaviour.clearCookiesOnStartup) {
			clearBrowserCookies();
			clearBrowserLocalStorage();
		}
		if (Behaviour.maximizeBrowserWindow) {
			webdriver().driver().getWebDriver().manage().window().maximize();
			
//			Dimension resolution = new Dimension(1280, 720);
	//		webdriver().driver().getWebDriver().manage().window().setSize(resolution);
		}
	}

	private static void killUnwantedProcess() {
		// NOTE - these do not close the open db sessions
		Runtime rt = Runtime.getRuntime();
		try {
			log.info("Killing legacy browser processes");
//	            if (!ConstantsProvider.isSELENIUM_GRID_ACTIVE()) {
			rt.exec("taskkill /f /im iexplore.exe");
			rt.exec("taskkill /f /im IEDriverServer-32-3.9.0.exe");
			
			rt.exec("taskkill /f /im firefox.exe");
			rt.exec("taskkill /f /im geckodriver-64bits.exe");
			
			//rt.exec("taskkill /f /im chrome.exe");
			//rt.exec("taskkill /f /im chromedriver.exe");
			
			rt.exec("taskkill /f /im msedge.exe");
			rt.exec("taskkill /f /im msedgedriver.exe");
			rt.exec("taskkill /f /im msedgewebview2.exe");
			
			rt.exec("taskkill /f /im dllhost.exe");
			// }
		} catch (IOException e) { /* ignore */	}
	}

	public static String openUrl(String url) {
		
		initialise();
		
		// new WINDOW
		if (!isActive()) {
			open(url);
			configure();
			
		// new TAB
		} else {		
			executeJavaScript("window.open(\"" + url + "\")");
		}

		// switch selenium focus to active window
		String activeTab = getWindowHandle();
		
		// log
		log.debug(System.lineSeparator() + System.lineSeparator() + "openUrl " + url);
		
		// return window handle
		return activeTab;

	}
	
	public static String getWindowHandle() {
		Object[] handles = webDriver().getWindowHandles().toArray();
		String activeTab = handles[handles.length-1].toString();
		switchTab(activeTab);
		return activeTab;
	}

	public static void switchTab(String activeTab) {
		webDriver().switchTo().window(activeTab);
	}
	public static void switchTab(int index) {
		switchTo().window(index);
	}

	
	
	public static void set(By bySelector, String value) {
		set(bySelector, value, false);
	}
	public static void set(By bySelector, String value, boolean exactMatch) {
		SelenideElement element = find(bySelector);	
		_set(element, value, exactMatch);
	}
	private static int isNull(String value, int defaultValue) {
		return (value == null ? defaultValue : Integer.parseInt(value));
	}	
	private static boolean isNull(String value, boolean defaultValue) {
		return (value == null ? defaultValue : Boolean.parseBoolean(value));
	}
	private static String isNull(String value, String defaultValue) {
		return (value == null ? defaultValue : value);
	}


	private static void _set(SelenideElement element, String value, boolean exactMatch) {
		
		log.info("   * _set(): 1");
		
		// suitable
		element.shouldBe(visible, enabled);		
		
		// element attributes
		String elementType = element.getTagName();
		String actualValue = _get(element);
		String role = isNull(element.getAttribute("role"), "");
		String type = isNull(element.getAttribute("type"), "");
		int maxLength = isNull(element.getAttribute("maxlength"), 99999);
		boolean required = isNull(element.getAttribute("required"), false);		
		//boolean trigger = element.getAttribute("class").contains("mat-autocomplete-trigger");			//mat-autocomplete-trigger 		mat-tooltip-trigger		
		boolean trigger = element.parent().$(By.cssSelector("[class*='hot-field'")).exists();

		log.info("   * _set(): 2");
		
		// check max-length
		if (value.length() > maxLength) {
			Assert.fail("set(): ERROR, failed to set element (" + element.getSearchCriteria() + "), given value(" + value + ") > max length(" + maxLength + ")"); 
		}					

		// set (or clear)
		if (value.length() > 0 || actualValue.length() > 0) {		

			// <input/>
			if (elementType.equals("input")) {
				
				if (type.equals("file")) {
					element.sendKeys(value);
					actualValue = value; // browser will return path as c:/fakepath for security 
				} else {
					//log.error("element.setValue " + value);
					log.info("   * _set(): 3");
					element.setValue(value);
					waitForSpinner(element);
					actualValue = _get(element);
				}

				
			// <mat-select/>
			} else if (elementType.equals("mat-select")) {
				setMatSelect(element, value, exactMatch);
				actualValue = _get(element); //.getText().trim();
				
			}
			else if (elementType.equals("select")) {
				setSelect(element, value);
				actualValue = _get(element); //.getText().trim(); //.getText().trim();
				
				
			// other!
			} else {
				Assert.fail("set(): Unknown element type (" + elementType + ")");
			}

			
			// can't insist to close the drop-down because sometimes this close a pop-up form (ESC or ENTER)
			/* ensure combo is closed
			if (role.equals("combobox")) {
				boolean expanded = isNull(element.getAttribute("aria-expanded"), false);
				if (expanded) {
					element.pressEscape();
					waitForSpinner(element);
					waitForProgressBar();
					expanded = isNull(element.getAttribute("aria-expanded"), false);
				}
				element.shouldBe(attribute("aria-expanded", "false"));
			}
			*/
			

		// required?
		} else if (required) {
			Assert.fail("set(): ERROR, failed to set element (" + element.getSearchCriteria() + "), mandatory value required"); 
		}

		
		// input fields with small red square trigger data population in other fields
		if (trigger) {
			//log.error("trigger before");
			element.pressTab();
			waitForSpinner(element);
			waitForProgressBar(30);
			actualValue = _get(element);			
			//log.error("trigger end");
		}
			
		
		// check value
		if (!(exactMatch ? actualValue.equals(value) : actualValue.startsWith(value))) {
			String msg = "set(): FAILED to set element (" + element.getSearchCriteria() + ") with value '" + value + "'. Actual value is '" + actualValue + "')";
			log.error(msg);
			Assert.fail(msg);
		}
		
		// log
		if (type.equals("password")) {
			actualValue = "********";		
			value = "********";
		}
		String msg = "set(): " + getCallingPropertyName() + " = '" + actualValue + "' (" + value + ")";
		if (trigger) msg += " {trigger}";
		log.info(msg);			
	}

	private static void waitForSpinner(SelenideElement element) {
		StopWatch timer = new StopWatch();
		timer.start();
		element.parent().parent().$(By.cssSelector("mat-spinner > svg")).shouldNot(exist);
		timer.stop();
		log.debug("   waitForSpinner(): " + timer.getTime());
	}

	
	public static void waitForSpinner(SelenideElement element, long timeoutSeconds) {
		long resetTimeout = Configuration.timeout;
		Configuration.timeout = timeoutSeconds * 1000;
		waitForSpinner(element);
		Configuration.timeout = resetTimeout;
	} 
	
	public static void waitForProgressBar(long timeoutSeconds) {
		long resetTimeout = Configuration.timeout;
		Configuration.timeout = timeoutSeconds * 1000;
		waitForProgressBar();
		Configuration.timeout = resetTimeout;
	}
	
	
	public static void waitForProgressBar() {
		StopWatch timer = new StopWatch();
		timer.start();
		$(By.cssSelector("mat-progress-bar")).shouldNot(exist);
		timer.stop();
		log.debug("   waitForProgressBar(): " + timer.getTime());
	}

	private static String setSelect(SelenideElement element, String value) {
		element.selectOption(value);
		return "";
		
		
	}

	private static String setMatSelect(SelenideElement element, String value, boolean exactMatch) {
		
		// some elements can be hidden by the top-menu after scrollIntoView(), so scroll down a bit so it can be clicked
		scrollIntoView(element);

		// click to expand drop-down if necessary
		boolean expanded = isNull(element.getAttribute("aria-expanded"), false);
		if (!expanded) {
			element.pressEnter(); //click();
			element.shouldBe(attribute("aria-expanded", "true"));
		}
		
		// get list of items
		String bySelectorOptionPanel = String.format("#%s", element.getAttribute("aria-controls"));
		SelenideElement selectorOptionPanel = $(By.cssSelector(bySelectorOptionPanel));
		selectorOptionPanel.should(exist);
		ElementsCollection selectorOptions = selectorOptionPanel.$$(By.cssSelector("mat-option"));
		selectorOptions.shouldHave(CollectionCondition.sizeGreaterThan(0));
		
		// loop through items
		Iterable<SelenideElement> optionsList = selectorOptions.asFixedIterable();
		for (SelenideElement option : optionsList) {
			option.should(exist);

			// get value BEFORE click to avoid StaleElementReferenceException:
			// once clicked, the list elements will disappear 
			String itemValue = option.innerText().trim();
		
			// matching?
			if (exactMatch ? itemValue.equals(value) : itemValue.startsWith(value)) {

				// select
				option.click(); //.pressEnter();
				
				// wait till closed
				//String expanded1 = element.getAttribute("aria-expanded"); 
				//element.shouldBe(attribute("aria-expanded", "false"));

				// return matched value
				return itemValue.trim();
			}
		}
		
		// failed to set value
		Assert.fail("setMatSelect(): FAILED to find listitem matching data value '" + value + "' for field " + getCallingPropertyName());
		

		return "";
	}
	
	
	public static void sendKey(By bySelector, CharSequence... keysToSend) {
		SelenideElement element = find(bySelector);
		element.sendKeys(keysToSend);
	}
	
	public static void  test(By bySelector) {
		if (tryFind(bySelector)) {
			SelenideElement target = find(bySelector);
		
			ElementsCollection elements = target.$$("*");
			
			Iterable<SelenideElement> elementList = elements.asFixedIterable();
			for (SelenideElement element : elementList) {
				log.debug(element.getAccessibleName());
				log.debug(element.toString());
				log.debug("    ? " + (element.exists() ? "exists" : "does NOT exist"));
				if (element.exists()) {
					log.debug("    ? is " + (element.isDisplayed() ? "" : "NOT ") + "displayed");
					log.debug("    ? is " + (element.isEnabled() ? "" : "NOT ") + "enabled");
					log.debug("    ? is " + (element.isSelected() ? "" : "NOT ") + "selected");
					log.debug("    ? value=" + (element.getValue()));
				}
			}
		}
		
	}
	
	public static String get(By bySelector) {
		// find
		SelenideElement element = find(bySelector);
		// return
		return get(element);
	}
	
	public static String get(SelenideElement element) {

		// suitable
		element.shouldBe(visible);

		// get
		String value = _get(element);

		// log
		log.info("get(): " + getCallingPropertyName() + " '" + value + "'");

		// return
		return value;
	}

	private static String _get(SelenideElement element) {
		String elementType = element.getTagName();
		
		/*
		log.debug("_get(): elementType=" + elementType);
		log.debug("_get(): getValue=" + element.getValue());
		log.debug("_get(): getText=" + element.getText());
		log.debug("_get(): innerText=" + element.innerText());
*/
		
		switch (elementType) {
			case "input": return isNull(element.getValue(), "");
			default : return isNull(element.getText(), "");
		}
	}
	
	public static void tryClick(By bySelector) {
		_click(bySelector, false);
	}
	public static void click(By bySelector) {
		_click(bySelector, true);
	}
	public static void click(By bySelector, String textFilter) {
		_click(bySelector, true, textFilter);
	}
	private static void _click(By bySelector, boolean assertShouldBe, String textFilter) {
		SelenideElement element = find(bySelector, assertShouldBe, textFilter);
		_click(element, assertShouldBe);
	}
	private static void _click(By bySelector, boolean assertShouldBe) {
		SelenideElement element = find(bySelector, assertShouldBe);
		_click(element, assertShouldBe);
	}
	private static void _click(SelenideElement element, boolean assertShouldBe) {

		// suitable ?
		if (element == null) {
			return;
		} else if (assertShouldBe) {
			element.shouldBe(visible, enabled);
		} else if (!element.isEnabled() ) {
			log.warn("tryClick(): element is not ENABLED");
			return;
		} else if (!element.isDisplayed() ) {
			log.warn("tryClick(): element is not DISPLAYED");
			return;
		}
		
		// ensure clickable
		scrollIntoView(element);

		// click
		String elementType = element.getTagName();
		String role = isNull(element.getAttribute("role"), "");
		String type = isNull(element.getAttribute("type"), "");

		if (elementType.equals("button")) {
			// NB click() was failing for some popUp.Save(). For unknown reason it was clicking the shadowRoot element instead
			element.pressEnter();
		} else if (type.equals("checkbox")) {
			element.parent().click();
		} else {
			element.click();
		} 

		// wait for transition
		waitForProgressBar();

			
		// log
		log.info("click(): " + getCallingPropertyName());
	}
	
	
	
	
	
	/*
	 * Used to set a table item in the format 
	   <table>
	  		<tr>
	  			<td attribute=value>name<td/>
	  			<td>condition<td/>
	  			<td>value<td/>
	   		<tr/>
	   	 	<tr>
	  			<td>name<td/>
	  			<td>condition<td/>
	  			<td>value<td/>
	   		<tr/>
	   <table/>
	 */
	public static void set(By bySelector, String name, String condition, String value) {

		// get table & ensure ready
		SelenideElement table = find(bySelector);

		// build table identifier from text in 1st column of each row
		StringBuilder tableId = new StringBuilder();
		ElementsCollection columns = table.$$("td");
		for (int colIndex=0; colIndex< columns.size(); colIndex+=3) {
			String txt = _get(columns.get(colIndex));
			tableId.append(txt);
		}
		
		// build tableCache
		ElementsCollection rows = table.$$("tr");
		if (!tableCache.containsKey(tableId.toString())) {
			tableCache = new HashMap<String, Integer>();
			tableCache.put(tableId.toString(), -1);
			for (int rowIndex=0; rowIndex< rows.size(); rowIndex++) {
				SelenideElement row = rows.get(rowIndex);
				columns = row.$$("td");
				if (columns.size() > 0) {
					SelenideElement col = columns.get(0);
					//col.should(exist, visible);		
					String text = _get(col);
					tableCache.put(text, rowIndex);
				}
			}
		}

		// search tableCache
		if (tableCache.containsKey(name)) {

			// get row/columns
			int rowIndex = tableCache.get(name);
			SelenideElement row = rows.get(rowIndex);
			columns = row.$$("td");
			// set condition
			SelenideElement conditionInput = columns.get(1).$("mat-select"); 
			_set(conditionInput, condition, false);
			// set value
			SelenideElement valueInput = columns.get(2).$("input"); 
			_set(valueInput, value, false);
			// log
			log.debug("setTableItem(): " + name + " ... " + condition + " ... " + value);

		} else {
			// error
			log.error("setTableItem(): FAILED to find item '" + name + "'");
		}
		return;


	}
	
	
	private static void scrollIntoView(SelenideElement element) {

		int windowHeight = webdriver().driver().getWebDriver().manage().window().getSize().getHeight();		
		int y = element.getCoordinates().inViewPort().getY();
		//log.debug("scrollIntoView(): windowHeight = " + windowHeight);
		//log.debug("scrollIntoView(): y = " + y);
		//log.debug("scrollIntoView(): windowHeight - y = " + (windowHeight - y));
		
		if (y < 250) {
			element.scrollIntoView(true);
			executeJavaScript("window.scrollBy(0,-100)", "");
		} else if (windowHeight - y < 250) {
			element.scrollIntoView(false);
			executeJavaScript("window.scrollBy(0,100)", "");
		}
	}

	public static boolean isDisplayed(By bySelector) {
		try {
			//log.debug("isDisplayed(): " + $(bySelector).toString());
			return $$(bySelector).filter(visible).size() > 0;
		} catch (StaleElementReferenceException ex) {
			return false;
		}
	}
	
	public static boolean tryFind(By bySelector) {
		return tryFind(bySelector, true);
	}
	
	public static boolean tryFind(By bySelector, boolean verbose) {

		// intro
		if (verbose) {
			log.debug(System.lineSeparator());
			log.debug("tryFind(): " + bySelector);
		}
		
		//wait(1);

		// timer
		StopWatch timer = new StopWatch();
		timer.start();

		// find
		ElementsCollection elements = $$(bySelector);
		if (elements.size() ==0) {
			if (verbose) {
				log.debug("    ? does NOT exist");
			}
			return false;
		} else {
			if (verbose) {
				log.debug("    found " + elements.size() + " matching elements:");
			}
		}
		
		// loop around
		Iterable<SelenideElement> elementList = elements.asFixedIterable();
		for (SelenideElement element : elementList) {

			// info
			if (verbose) {
				log.debug(element.getAccessibleName());
				log.debug(element.toString());
				log.debug("    ? " + (element.exists() ? "exists" : "does NOT exist"));
				if (element.exists()) {
					log.debug("    ? is " + (element.isDisplayed() ? "" : "NOT ") + "displayed");
					log.debug("    ? is " + (element.isEnabled() ? "" : "NOT ") + "enabled");
					log.debug("    ? is " + (element.isSelected() ? "" : "NOT ") + "selected");
					log.debug("    ? value=" + (element.getValue()));
					log.debug("    ? found in " + timer.toString() + "ms");
				}
			}
		}
		return true;
	}

	
	
	public static String getList(By bySelector) {

	    StringBuilder listItems = new StringBuilder();
		ElementsCollection elementCollection = $$(bySelector).filter(exist).filter(visible);
		elementCollection.shouldHave(CollectionCondition.sizeGreaterThan(0));
		
		// loop through items
		Iterable<SelenideElement> elementList = elementCollection.asFixedIterable();
		for (SelenideElement element : elementList) {
			element.should(exist);
			listItems.append(_get(element)).append(System.lineSeparator());
		}
		return listItems.toString();
	}

	public static SelenideElement find(By bySelector) {
		return find(bySelector, true);
	}
	public static SelenideElement find(By bySelector, boolean assertShouldBe) {
		return find(bySelector, assertShouldBe, null);
	}
	public static SelenideElement find(By bySelector, boolean assertShouldBe, String textFilter) {

		// go slow for display
		if (Behaviour.runSlow) {
			sleep(1);
		}
		

				
		// find element
		SelenideElement element = (textFilter==null ? $(bySelector) : $$(bySelector).findBy(Condition.text(textFilter))); 
		
		// check "visable"
		if (!element.isDisplayed()) {
			scrollIntoView(element);
			
			if (assertShouldBe) {		
				ElementsCollection visibleElements = $$(bySelector).filter(visible);
				element = (textFilter==null ? visibleElements.get(0) : visibleElements.findBy(Condition.text(textFilter))); 
			} else {
				log.warn("find(): element " + getCallingPropertyName() + " is not visable (" + bySelector.toString() + ")");
			}
		}
		
		// check "exists" and return
		if (assertShouldBe) {		
			element.should(exist);
		} else if (!element.exists()) {
			log.warn("find(): element " + getCallingPropertyName() + " does not exist");
			element = null;
		}	
		
		//log.debug("   find(): found " + element.toString());
		return element;
	}
	
	

	
	private static String getCallingPropertyName() {
		// traverse stack trace until we find the call outside of webUI class
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String webUiClassName = stackTraceElements[1].getClassName();
		int index = 2;
		while (stackTraceElements[index++].getClassName().equals(webUiClassName));
		return stackTraceElements[index - 1].getFileName().replace("java", "") + stackTraceElements[index - 1].getMethodName();
	}
	

	public static void waitForUrlChange(String urlContains) {

		// timeout
		Duration timeout = Duration.ofSeconds(30);
		Duration polling = Duration.ofMillis(500);
		// log.debug("waitForUrlChange(): timeout is " + timeout.getSeconds() + "s,
		// polling is " + polling.getSeconds() + "s" );
		sleep(polling.toMillis());
					
		// get selenide web driver
		WebDriver webDriver = webDriver();
		if (!webDriver.getCurrentUrl().contains(urlContains)) {
			
			// define "wait"
			WebDriverWait wait = new WebDriverWait(webDriver, timeout, polling);
	
			// timer
			StopWatch timer = new StopWatch();
			timer.start();
	
			// execute "wait"		
			try {
				wait.until(ExpectedConditions.urlContains(urlContains));
				log.debug("   waitForUrlChange(" + webDriver.getCurrentUrl() + "): " + timer.getTime());
	
			// error
			} catch (TimeoutException ex) {
				log.error("waitForUrlChange(): TIMEOUT waiting for URL to contain '" + urlContains + "' [" + timer.getTime() / 1000 + "s]");
				Assert.fail(ex.getMessage());
			} catch (Exception ex) {
				log.error("waitForUrlChange(): ERROR waiting for URL to contain '" + urlContains + "'");
				Assert.fail(ex.getMessage());
			}
		}

	}

	public static void waitForTitleChange(String titleContains) {

		// get selenide web driver
		WebDriver webDriver = webDriver();

		// timeout
		Duration timeout = Duration.ofSeconds(60);
		Duration polling = Duration.ofSeconds(1);
		log.debug("waitForTitleChange(): timeout is " + timeout.getSeconds() + "s, polling is " + polling.getSeconds() + "s");

		// define "wait"
		WebDriverWait wait = new WebDriverWait(webDriver, timeout, polling);

		// timer
		StopWatch timer = new StopWatch();
		timer.start();

		// execute "wait"
		try {
			log.debug("currentTitle: " + webDriver.getTitle());
			log.debug("waitForTitleChange... " + titleContains);
			wait.until(ExpectedConditions.titleContains(titleContains));
			log.debug("waitForTitleChange(): " + titleContains + " [" + timer.getTime() + "ms]");
			log.debug("newTitle: " + webDriver.getTitle());

			// error
		} catch (Exception ex) {
			log.debug("waitForTitleChange(): TIMEOUT waiting for TITLE to contain " + titleContains + " ["
					+ timer.getTime() / 1000 + "s]");
			Assert.fail(ex.getMessage());
		}

	}

	public static void waitForElementValue(By bySelector, String targetValue) {
		waitForElementValue(bySelector, targetValue, 30);
	}
	public static void waitForElementValue(By bySelector, String targetValue, int timeoutSeconds) {
		// config
		long timeout = timeoutSeconds*1000; // 30sec
		long poll = 100;

		// timeout
		StopWatch timer = new StopWatch();
		timer.start();
		
		sleep(poll);
		
		// wait for target element value
		SelenideElement element = find(bySelector);
		while (!_get(element).equals(targetValue) && (timer.getTime()<timeout)) {
			sleep(poll);
		}
		
		// log
		timer.stop();
		if (timer.getTime() > poll) {
			log.debug("   waitForElementValue(" + getCallingPropertyName() + "." + bySelector + "=" + targetValue + "): waiting for " + timer.getTime() + "ms");
		}
		
		// timeout
		if (timer.getTime() >= timeout) {
			Assert.fail("   waitForElementValue(" + getCallingPropertyName() + "." + bySelector + "=" + targetValue + "): TIMEOUT after " + timeout + "ms");
		} 
	}

	public static void waitForElement(By bySelector, boolean targetExistCondition) {
		waitForElement(bySelector, targetExistCondition, 30);
	}

	public static void waitForElement(By bySelector, boolean targetExistCondition, int timeoutSeconds) {

		// config
		long timeout = timeoutSeconds*1000; // 30sec
		long poll = 100;

		// timeout
		StopWatch timer = new StopWatch();
		timer.start();
		
		sleep(poll);
		
		// wait for target element to EXIST
		if (targetExistCondition) {
			while (!isDisplayed(bySelector) && (timer.getTime()<timeout)) {
				sleep(poll);
			}
		// wait for target element to DISAPEAR
		} else {
			while (isDisplayed(bySelector) && (timer.getTime()<timeout)) {
				sleep(poll);
			}
		}
		
/*
	
		// wait for target element to EXIST/DISAPEAR
		while ((isDisplayed(bySelector) != targetExistCondition) && (timer.getTime()<timeout)) {
			sleep(poll);
		}
		*/
		
		// log
		timer.stop();
		if (timer.getTime() > poll) {
			log.debug("   waitForElement(" + getCallingPropertyName() + "." + bySelector + "): waiting for " + timer.getTime() + "ms");
		}
		
		// timeout
		if (timer.getTime() >= timeout) {
			Assert.fail("   waitForElement(" + getCallingPropertyName() + "." + bySelector + "): TIMEOUT after " + timeout + "ms");
		} 
		

	}

	public static void sleep (long millis) {
		try {
			Thread.sleep(millis);
			Thread.yield();
		} catch (InterruptedException e) {	/* ignore */ }
	}

	private static WebDriver webDriver() {
		// get selenide web driver
		return webdriver().driver().getWebDriver();
	}
	
	public static boolean isActive() {
		return webdriver().driver().hasWebDriverStarted();
	}
	
	public static void close() {
		Steps.browserTabs.values().remove(getWindowHandle());
		closeWindow();
		getWindowHandle();
	}

	public static void closeAll() {
		log.debug("getWebDriverLogs():");
		List<String> logs = getWebDriverLogs(LogType.BROWSER);
		log.debug(logs.toString());
		logs = getWebDriverLogs(LogType.CLIENT);
		log.debug(logs.toString());
		logs = getWebDriverLogs(LogType.DRIVER);
		log.debug(logs.toString());
	//	logs = getWebDriverLogs(LogType.PERFORMANCE);
	//	log.debug(logs.toString());
	//	logs = getWebDriverLogs(LogType.PROFILER);
	//	log.debug(logs.toString());
	//	logs = getWebDriverLogs(LogType.SERVER);
	//	log.debug(logs.toString());

		
		// close driver & window
		closeWindow();
		closeWebDriver();
		if (!Configuration.headless) {
			killUnwantedProcess();
		}
	}

	
	
	
	
//	@Test
	public void userCanLoginByUsername() {

		// https://selenide.org/javadoc/5.6.0/com/codeborne/selenide/Configuration.html
		// Configuration.driverManagerEnabled =false;

		/*
		 * see SeleniumDriverProvider.java for more examples
		 */

		// Configuration.browser = "chrome";
		// System.setProperty("webdriver.chrome.driver",
		// "resources\\drivers\\chromedriver.exe");
		Configuration.browser = "chrome";
		System.setProperty("webdriver.edge.driver", "resources\\drivers\\msEdgeDriver.exe");

		open("http://sdcetad007.ent.ad.ntrs.com:40202/frlaunch/frhome.jsp?config=DEV_APP_gta_eu");

		$(By.name("username")).setValue(ConstantsProvider.getAPI_UID());
		$(By.name("password")).setValue(Encryption.decrypt(ConstantsProvider.getAPI_PWD()));

		$(By.xpath("//input[@alt='Submit']")).click();

	}

	

	

	
	/*
	public static RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor) {
		CommandExecutor executor = new HttpCommandExecutor(command_executor) {

			@Override
			public Response execute(Command command) throws IOException {
				Response response = null;
				if (command.getName() == "newSession") {
					response = new Response();
					response.setSessionId(sessionId.toString());
					response.setStatus(0);
					response.setValue(Collections.<String, String>emptyMap());

					try {
						Field commandCodec = null;
						commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
						commandCodec.setAccessible(true);
						commandCodec.set(this, new W3CHttpCommandCodec());

						Field responseCodec = null;
						responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
						responseCodec.setAccessible(true);
						responseCodec.set(this, new W3CHttpResponseCodec());
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				} else {
					response = super.execute(command);
				}
				return response;
			}
		};

		return new RemoteWebDriver(executor, new DesiredCapabilities());
	}

	public static void getOpenWebDriver() {

		ChromeDriver driver = new ChromeDriver();

		
		HttpCommandExecutor executor = (HttpCommandExecutor) driver.getCommandExecutor();
		URL url = executor.getAddressOfRemoteServer();
		SessionId session_id = driver.getSessionId();

		RemoteWebDriver driver2 = createDriverFromSession(session_id, url);
		driver2.get("http://tarunlalwani.com");
	}
	*/

}
