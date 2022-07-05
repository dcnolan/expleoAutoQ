package com.expleoautomation.utils;

import com.google.common.base.Strings;
import com.paulhammant.ngwebdriver.NgWebDriver;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Browser utils. all methods require the iSmart to be displayed beforehand */
@Log4j2
public class BrowserUtils {

  private JavascriptExecutor javascriptExecutor;
  private WebDriver webDriver;
  private WebDriverWait wait;
  private static NgWebDriver ngwebDriver;
  

  public BrowserUtils(WebDriver webDriver) {
    this.webDriver = webDriver;
    this.wait = new WebDriverWait(webDriver, 15);
    webDriver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
    if (webDriver instanceof JavascriptExecutor) {
      javascriptExecutor = (JavascriptExecutor) webDriver;
    } else {
      throw new IllegalArgumentException("provided webdriver can't execute " + "js commands");
    }
    ngwebDriver = new NgWebDriver(javascriptExecutor);
  }

 public static boolean isUiExpectedCondition(
      WebDriver webDriver, Function isTrue, int timeOutInSeconds) {
    return isUiExpectedCondition(webDriver, isTrue, timeOutInSeconds, "");
  }

  public static boolean isUiExpectedCondition(
      WebDriver webDriver, Function isTrue, int timeOutInSeconds, String description) {
    if (!Strings.isNullOrEmpty(description)) log.debug(description);
    try {
      new WebDriverWait(webDriver, timeOutInSeconds).ignoring(WebDriverException.class).until(isTrue);
    } catch (WebDriverException wde) {
      return false;
    }
    return true;
  }

  /**
   * Selects a random element in a select and returns the selected value. This method doesn't
   * consider 'Please Select' to be a correct option
   *
   * @param selectAsWebElement is a web element that has the tag name Select. This method accounts
   *     for the case when the element initially has another tag name
   * @return the selected entry in the select
   */
  /*public static String selectRandomElementInSelectWebElement(
      WebDriver webDriver, WebElement selectAsWebElement) {
    selectAsWebElement =
        new WebDriverWait(webDriver, 5)
            .ignoring(StaleElementReferenceException.class)
            .until(ExpectedConditionsLazyWebElement.visibilityOf(selectAsWebElement, "select"));
    Select select = new Select(selectAsWebElement);
    List<WebElement> allOptions =
        select.getOptions().stream()
            .filter(
                option -> {
                  String selectText = option.getText().toLowerCase();
                  return !(selectText.equals("please select") || selectText.contains("no change"));
                })
            .collect(Collectors.toList());
    String randomOptionText = CollectionUtils.getRandomElementFromCollection(allOptions).getText();
    log.debug("select entry in select element: " + randomOptionText);
    select.selectByVisibleText(randomOptionText);
    return randomOptionText;
  }*/

 
  /*public Object executeJsOnBrowser(String jsCall, boolean waitForLoadingIcon) {
    log.debug("Execute call on browser " + jsCall);
    Object result = javascriptExecutor.executeScript("return " + jsCall);
    if (result == null) {
      result = "";
    }
    if (waitForLoadingIcon) {
      PageFactory.initElements(webDriver, ISmartPage.class).waitTillIsmartLoadingFinish();
    }
    return result;
  }*/
  

	 /**********************************************************************************
	 **ACTION METHODS
	 **********************************************************************************/

	public static void actionMoveAndClick(WebDriver driver,WebElement element) throws Exception {
		Actions ob = new Actions(driver);
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element)).isEnabled();
			ob.moveToElement(element).click().build().perform();
			System.out.println("Successfully Action Moved and Clicked on the WebElement, using locator: " + "<" + element.toString() + ">");
		} catch (StaleElementReferenceException elementUpdated) {
			WebElement elementToClick = element;
			Boolean elementPresent = new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(elementToClick)).isEnabled();
			if (elementPresent == true) {
				ob.moveToElement(elementToClick).click().build().perform();
				System.out.println("(Stale Exception) - Successfully Action Moved and Clicked on the WebElement, using locator: " + "<" + element.toString() + ">");
			}
		} catch (Exception e) {
			System.out.println("Unable to Action Move and Click on the WebElement, using locator: " + "<" + element.toString() + ">");
			//Assert.fail("Unable to Action Move and Click on the WebElement, Exception: " + e.getMessage());
		}
	}

	public static void actionMoveAndClickByLocator(WebDriver driver,By element) throws Exception {
		Actions ob = new Actions(driver);
		try {
			Boolean elementPresent = new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element)).isEnabled();
			if (elementPresent == true) {
				WebElement elementToClick = driver.findElement(element);
				ob.moveToElement(elementToClick).click().build().perform();
				System.out.println("Action moved and clicked on the following element, using By locator: " + "<" + element.toString() + ">");
			}
		} catch (StaleElementReferenceException elementUpdated) {
			WebElement elementToClick = driver.findElement(element);
			ob.moveToElement(elementToClick).click().build().perform();
			System.out.println("(Stale Exception) - Action moved and clicked on the following element, using By locator: "+ "<" + element.toString() + ">");
		} catch (Exception e) {
			System.out.println("Unable to Action Move and Click on the WebElement using by locator: " + "<" + element.toString() + ">");
			//Assert.fail("Unable to Action Move and Click on the WebElement using by locator, Exception: " + e.getMessage());
		}
	}
	
	public static void keyPress(WebDriver driver, Keys keys) throws Exception {
		Actions ob = new Actions(driver);
		try {
				ob.sendKeys(keys).build().perform();
				System.out.println(keys.toString() + "pressed");
			}
		
		 catch (Exception e) {
			 System.out.println(keys.toString() + "pressed");
		}
	}
	
	

	/**********************************************************************************/
	/**********************************************************************************/
  
  /**********************************************************************************
	 **CLICK METHODS /
	 **********************************************************************************/
  public static void waitAndClickElement(WebDriver driver,WebElement element) throws InterruptedException, IOException {
		boolean clicked = false;
		int attempts = 0;
		while (!clicked && attempts < 10) {
			try {
				new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element)).click();
				System.out.println("Successfully clicked on the WebElement: " + "<" + element.toString() + ">");
				log.debug("Successfully clicked on the WebElement: " + "<" + element.toString() + ">");
				//element.click();
				clicked = true;
			} catch (Exception e) {
				System.out.println("Unable to wait and click on WebElement, Exception: " + e.getMessage());
				Assert.fail("Unable to wait and click on the WebElement, using locator: " + "<" + element.toString() + ">");
			}
			attempts++;
		}
	}
  
  public static void waitAndclickElementUsingJS(WebDriver driver,WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element));
			js.executeScript("arguments[0].click();", element);
			System.out.println("Successfully JS clicked on the following WebElement: " + "<" + element.toString() + ">");
			log.debug("Successfully JS clicked on the following WebElement: " + "<" + element.toString() + ">");
		} catch (StaleElementReferenceException elementUpdated) {
			WebElement staleElement = element;
			Boolean elementPresent = new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(staleElement)).isEnabled();
			if (elementPresent == true) {
				js.executeScript("arguments[0].click();", elementPresent);
				System.out.println("(Stale Exception) Successfully JS clicked on the following WebElement: " + "<" + element.toString() + ">");
			}
		} catch (NoSuchElementException e) {
			System.out.println("Unable to JS click on the following WebElement: " + "<" + element.toString() + ">");
			//Assert.fail("Unable to JS click on the WebElement, Exception: " + e.getMessage());
		}
	}

	public static void jsClick(WebDriver driver,WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}
	
	public static void clickElement(WebDriver driver,WebElement element) throws InterruptedException, IOException {
			try {
				new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(element));
				element.click();
				System.out.println("Successfully clicked on the WebElement: " + "<" + element.toString() + ">");
				log.debug("Successfully clicked on the WebElement: " + "<" + element.toString() + ">");
				} 
			catch (Exception e) {
				System.out.println("Unable to click on WebElement, Exception: " + e.getMessage());
				Assert.fail("Unable to click on the WebElement, using locator: " + "<" + element.toString() + ">");
			}
			
	}  
  /**********************************************************************************
	 **SEND KEYS METHODS /
	 **********************************************************************************/
	public static void sendKeysToWebElement(WebDriver driver,WebElement element, String textToSend) throws Exception {
		try {
			WaitUntilWebElementIsVisible(driver,element);
			element.clear();
			element.sendKeys(textToSend);
			log.debug("Successfully Sent the following keys: '" + textToSend + "' to element: " + "<"+ element.toString() + ">");
			System.out.println("Successfully Sent the following keys: '" + textToSend + "' to element: " + "<"+ element.toString() + ">");
		} catch (Exception e) {
			System.out.println("Unable to locate WebElement: " + "<" + element.toString() + "> and send the following keys: " + textToSend);
			//Assert.fail("Unable to send keys to WebElement, Exception: " + e.getMessage());
		}
	}
	
	/**********************************************************************************
	 **WAIT METHODS
	 **********************************************************************************/
	public static boolean WaitUntilWebElementIsVisible(WebDriver driver,WebElement element) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(element));
			log.debug("WebElement is visible using locator: " + "<" + element.toString() + ">");
			System.out.println("WebElement is visible using locator: " + "<" + element.toString() + ">");
			return true;
		} catch (Exception e) {
			System.out.println("WebElement is NOT visible, using locator: " + "<" + element.toString() + ">");
			//Assert.fail("WebElement is NOT visible, Exception: " + e.getMessage());
			return false;
		}
	}

	public static boolean WaitUntilWebElementIsVisibleUsingByLocator(WebDriver driver,By by) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOfElementLocated(by));
			log.debug("Element is visible using By locator: " + "<" + by.toString() + ">");
			System.out.println("Element is visible using By locator: " + "<" + by.toString() + ">");
			return true;
		} catch (Exception e) {
			System.out.println("WebElement is NOT visible, using By locator: " + "<" + by.toString() + ">");
			//Assert.fail("WebElement is NOT visible, Exception: " + e.getMessage());
			return false;
		}
	}

	public static boolean WaitUntilWebElementClickable(WebDriver driver,WebElement element) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element));
			log.debug("WebElement is clickable using locator: " + "<" + element.toString() + ">");
			System.out.println("WebElement is clickable using locator: " + "<" + element.toString() + ">");
			return true;
		} catch (Exception e) {
			System.out.println("WebElement is NOT clickable using locator: " + "<" + element.toString() + ">");
			return false;
		}
	}

	public static boolean WaitUntilWebElementClickableUsingByLocator(WebDriver driver,By by) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(by));
			log.debug("WebElement is clickable using locator: " + "<" + by.toString() + ">");
			System.out.println("WebElement is clickable using locator: " + "<" + by.toString() + ">");
			return true;
		} catch (Exception e) {
			System.out.println("WebElement is NOT clickable using locator: " + "<" + by.toString() + ">");
			return false;
		}
	}
	
	public static boolean WaitUntilWebElementsAreVisible(WebDriver driver,List<WebElement> elements) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOfAllElements(elements));
			log.debug("WebElement is clickable using locator: " + "<" + elements.toString() + ">");
			System.out.println("WebElement is clickable using locator: " + "<" + elements.toString() + ">");
			return true;
		} catch (Exception e) {
			System.out.println("WebElement is NOT clickable using locator: " + "<" + elements.toString() + ">");
			return false;
		}
	}
	

	public boolean waitUntilPreLoadElementDissapears(By element) {
		return this.wait.until(ExpectedConditions.invisibilityOfElementLocated(element));
	}
	
	/**********************************************************************************
	 **GETTING ATTRIBUTE VALUE FROM ELEMENTS
	 **********************************************************************************/
	public static String getText(WebDriver driver,WebElement element) {
		String strText = null;
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(element));
			strText = element.getText().trim();
			log.debug("Text with in the Element: " + "<" + strText + ">");
			System.out.println("Text with in the Element: " + "<" + strText + ">");
			return strText;
		} catch (NullPointerException e) {
			System.out.println("unable to get the text from the WebElement using locator: " + "<" + element.toString() + ">");
		}
		return strText;
	}
	
	public static String getValueFromInputBox(WebDriver driver,WebElement element) {
		String strText = null;
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(element));
			strText = element.getAttribute("value").trim();
			log.debug("value with in the Element: " + "<" + strText + ">");
			System.out.println("value with in the Element: " + "<" + strText + ">");
			return strText;
		} catch (NullPointerException e) {
			System.out.println("unable to get the value from the WebElement using locator: " + "<" + element.toString() + ">");
		}
		return strText;
	}

	/**********************************************************************************
	 **NGWEBDRIVER METHODS
	 * @return 
	 **********************************************************************************/
	
	public static void waitForAngularToLoad(WebDriver driver)
	{
		JavascriptExecutor javascriptExecutor;
		javascriptExecutor = (JavascriptExecutor) driver;
		ngwebDriver = new NgWebDriver(javascriptExecutor);
		ngwebDriver.waitForAngularRequestsToFinish();
		
	}
	

	/**********************************************************************************
	 **METHODS FOR ANGULAR APPLICATIONS
	 **********************************************************************************/
	
	/**********************************************************************************
	 **METHODS FOR "YES(0)" OR "NO(1)" RADIO BUTTON FROM RADIO GROUP
	 **********************************************************************************/

	public static void clickYesRadioButtonFromRadioGroup(WebDriver driver,WebElement element, By radioButtonLocator) throws InterruptedException, IOException
	{
		try {
		     WaitUntilWebElementIsVisible(driver,element);
		     List<WebElement> radioButtons = element.findElements(radioButtonLocator);
		     clickElement(driver,radioButtons.get(0));
	 
	      } 
		catch (Exception e) {
		     System.out.println("Unable to click on the radio button, Exception: " + e.getMessage());
		}
			
	}
	
	public static void clickNoRadioButtonFromRadioGroup(WebDriver driver,WebElement element,By radioButtonLocator) throws InterruptedException, IOException
	{
		try {
		     WaitUntilWebElementIsVisible(driver,element);
		     List<WebElement> radioButtons = element.findElements(radioButtonLocator);
		     clickElement(driver,radioButtons.get(1));
	      } 
		catch (Exception e) {
		     System.out.println("Unable to click on the radio button, Exception: " + e.getMessage());
		}
			
	}
	
	public static void selectDataFromMultiSelect(WebDriver driver) throws Exception
	{
			By matOptionsLocator = By.xpath("//mat-option[@role='option']");
			WaitUntilWebElementClickableUsingByLocator(driver,matOptionsLocator);
			List<WebElement> matOptions = driver.findElements(matOptionsLocator);
			WaitUntilWebElementsAreVisible(driver,matOptions);
			for (int i = 0; i < matOptions.size(); i++) {
				WaitUntilWebElementClickable(driver,matOptions.get(i));
				WebElement matText = matOptions.get(i).findElement(By.xpath("//span[@class='mat-option-text']"));
				if (matText.getText()!="Other") {
					clickElement(driver,matOptions.get(i));
					log.debug("Option" + matText.getText() + "is selected");
					if (i == 1) {
						break;
				    }
				}
			}
		keyPress(driver,Keys.TAB);
	}
	
	public static void selectUnCheckedDataFromMultiSelect(WebDriver driver) throws Exception
	{
			By matOptionsLocator = By.xpath("//mat-option[@role='option'][@aria-selected='false']");
			WaitUntilWebElementClickableUsingByLocator(driver,matOptionsLocator);
			List<WebElement> matOptions = driver.findElements(matOptionsLocator);
			WaitUntilWebElementsAreVisible(driver,matOptions);
			for (int i = 0; i < matOptions.size(); i++) {
				WaitUntilWebElementClickable(driver,matOptions.get(i));
				WebElement matTextElement = driver.findElement(By.xpath("//mat-option[@role='option'][@aria-selected='false'][1]//span[@class='mat-option-text']"));
		        String matText = matTextElement.getText().trim();
				System.out.println(matText);
				if (!matText.equals("Other")) {
					clickElement(driver,matTextElement);
					log.debug("Option" + matText + "is selected");
					if (i == 1) {
						break;
				    }
				}
			}
		keyPress(driver,Keys.TAB);
	}
	
	
	/**********************************************************************************
	 **GETTING ROW BY CELL TEXT IN A TABLE
	 **********************************************************************************/
	  
	  public static int getRowbyCellText(WebDriver driver,String tableLocator, String compareString)
	  {
		  String strCellVal = null;
		  int row = 0;
			  try {
			  WebElement table=driver.findElement(By.xpath(tableLocator));
			  WaitUntilWebElementIsVisible(driver, table);
			  List <WebElement> rows = table.findElements(By.tagName("tr"));
			  for (int i = 1;i<=rows.size(); i++) {
				  row =i;
			 	List <WebElement> col= table.findElements(By.xpath(tableLocator+"/tr["+row+"]/td"));
				for (int j = 1; j <=col.size(); j++) {
					strCellVal= driver.findElement(By.xpath(tableLocator+"/tr["+row+"]/td["+j+"]")).getText();
					if (strCellVal!=null&&strCellVal!="") {
						if (strCellVal.contains(compareString)) {
						break;
							}
					}
				}	
					if (strCellVal!=null&&strCellVal!="") {
						
						  if (strCellVal.contains(compareString)) {
							break;
							}
						}
			  }
			   }
		  catch (Exception e) {
		  e.printStackTrace();
		  System.out.println(e.getMessage());
		}
		return row;
		
	}
	
	  /**********************************************************************************
		 **GETTING ROW COUNT FROM A TABLE
	   **********************************************************************************/
	  public static int tableRowCount(WebDriver driver,By byTableLocator){
	     int rowcount = 0;
			try {
			      WebElement table=driver.findElement(byTableLocator);
			      List <WebElement> rows = table.findElements(By.tagName("tr"));
	              log.debug("No of rows present in table is "+rows.size() );
	              rowcount=rows.size();
	              return rowcount;
			  }
			  catch(Exception e){
				  log.debug(e.getMessage());
			  }
			  return rowcount;
	}
	
	  /**********************************************************************************
		 **WAITING FOR PAGE TO LOAD
	   **********************************************************************************/
	  public static void waitForPageLoad(WebDriver driver,int timeout)  {
		   WebDriverWait wait = new WebDriverWait(driver, timeout);
		   ExpectedCondition<Boolean> pageLoadCondition = new
		   ExpectedCondition<Boolean>() {
		   public Boolean apply(WebDriver driver) {
				  return ((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete");
				  }
		    };
		  wait.until(pageLoadCondition);
	  }
	  
	  /**********************************************************************************
		 **ANGULAR WAITS
	   **********************************************************************************/
	  
	  public static void waitForAngularLoad(WebDriver driver) {
	        String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";
	        angularLoads(angularReadyScript,driver);
	    }
	  
	  public static void waitUntilAngularReady(WebDriver driver) {
	        try {
	            Boolean angularUnDefined = (Boolean) ((JavascriptExecutor)driver).executeScript("return window.angular === undefined");
	            if (!angularUnDefined) {
	                Boolean angularInjectorUnDefined = (Boolean) ((JavascriptExecutor)driver).executeScript("return angular.element(document).injector() === undefined");
	                if (!angularInjectorUnDefined) {
	                    poll(20);
		                waitForAngularLoad(driver);
	 	                poll(20);
	                }
	            }
	        } catch (WebDriverException ignored) {
	        }
	    }
	 
	    public static void waitUntilAngular5Ready(WebDriver driver) {
	        try {
	            Object angular5Check = ((JavascriptExecutor)driver).executeScript("return getAllAngularRootElements()[0].attributes['ng-version']");
	            if (angular5Check != null) {
	                Boolean angularPageLoaded = (Boolean) ((JavascriptExecutor)driver).executeScript("return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) ===-1");
	                	if (!angularPageLoaded) {
	                    poll(10);
	                    waitForAngular5Load(driver);
	 	                poll(10);
	                }
	            }
	        } catch (WebDriverException ignored) {
	        }
	    }
	 
	    public static void waitForAngular5Load(WebDriver driver) {
	        String angularReadyScript = "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1";
	        angularLoads(angularReadyScript,driver);
		}
	 
	    public static void angularLoads(String angularReadyScript, WebDriver driver) {
	        try {
	            ExpectedCondition<Boolean> angularLoad = Webdriver -> Boolean.valueOf(((JavascriptExecutor) driver)
	                .executeScript(angularReadyScript).toString());
	           	 boolean angularReady = Boolean.valueOf(((JavascriptExecutor)driver).executeScript(angularReadyScript).toString());
		         if (!angularReady) {
	            	new WebDriverWait(driver,5).until(angularLoad);
	            }
	        } catch (WebDriverException ignored) {
	        	System.out.println(ignored.getMessage());
	        }
	    }
	    
	    public static void poll(long milis) {
	        try {
	            Thread.sleep(milis);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * Method to make sure a specific element has loaded on the page
	     *
	     * @param by
	     * @param expected
	     */
	    public void waitForElementAreComplete(By by, int expected,WebDriver driver) {
	        ExpectedCondition<Boolean> angularLoad = WebDriver -> {
	            int loadingElements =driver.findElements(by).size();
	            return loadingElements >= expected;
	        };
	        new WebDriverWait(webDriver, 15).until(angularLoad);
	    }
	
	/*******************************************************************************************
	 CHECK IF ELEMENT EXISITS OR NOT
	 *******************************************************************************************/
	    public static Boolean IsElementPresent(WebDriver driver,By by) 
	    {
	        boolean isPresent = false;
	   	 try
	        {   
	   		 driver.findElement(by);
	       	 isPresent = true;
	   	     return isPresent;
	         }
	        catch (NoSuchElementException ex)
	        {
	       	 isPresent = false;
	       	 return isPresent;
	        }
	        
	        catch (ElementNotVisibleException ex)
	        {
	       	 isPresent = false;
	       	 return isPresent;
	        }
	        catch (InvalidElementStateException ex)
	        {
	       	 isPresent = false;
	       	 return isPresent;
	        }
	        
	    }	

	    
	    

}
