
/* NB - deprecated, here for reference */

package com.expleoautomation.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.assertj.core.api.Assumptions;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.expleoautomation.commons.ConstantsProvider;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SeleniumDriverProvider {

    private static boolean isSeleniumGridReachable = true;
    @Getter private static String browserName;
    private WebDriver webDriver;

    public SeleniumDriverProvider() {
    }

    public SeleniumDriverProvider(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * This will skip the tests if onGrid is true while the selenium hub is not reachable. This will happen for the
     * current scenario and all remaining scenarios
     */
    public EventFiringWebDriver getWebDriver(@NonNull String browserName, boolean onGrid,
        boolean enableLogging) throws Exception {
        SeleniumDriverProvider.browserName = browserName;
        WebDriver webDriver;
        if (onGrid) {
            webDriver = getRemoteWebDriver(ConstantsProvider.getSELENIUM_HUB_URL(), browserName,
                enableLogging);
        } else {
            webDriver = getLocalWebDriver(browserName, enableLogging);
        }
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(webDriver);
        WebEventListener eventListener = new WebEventListener();
        eventFiringWebDriver.register((WebDriverEventListener) eventListener);
        webDriver.manage().timeouts().implicitlyWait(ConstantsProvider.getGLOBAL_TIMEOUT(), TimeUnit.SECONDS);
        webDriver.manage().window().maximize();
        this.webDriver = webDriver;
        return eventFiringWebDriver;
    }

    public List<LogEntry> getPerformanceLogs() {
        return webDriver.manage().logs().get(LogType.PERFORMANCE).getAll();
    }

    public List<LogEntry> getClientLogs() {
        return webDriver.manage().logs().get(LogType.CLIENT).getAll();
    }

    public List<LogEntry> getServerLogs() {
     return webDriver.manage().logs().get(LogType.SERVER).getAll();
     }

    public List<LogEntry> getBrowserLogs() {
        return webDriver.manage().logs().get(LogType.BROWSER).getAll();
    }

    private WebDriver getRemoteWebDriver(@NonNull String seleniumHubUrl, @NonNull String browserName,
        boolean enableLogging) throws MalformedURLException {
        Assumptions.assumeThat(isSeleniumGridReachable).isTrue();
        try {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ConstantsProvider.getSELENIUM_VM_IP(), ConstantsProvider.getSELENIUM_HUB_PORT()), 300);
            }
        } catch (IOException e) {
            isSeleniumGridReachable = false;
            Assumptions.assumeThat(false).as("selenium grid reachable").isTrue();
        }
        DesiredCapabilities capabilities = getDesiredCapabilities(browserName, enableLogging);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);

        return new RemoteWebDriver(new URL(seleniumHubUrl), capabilities);
    }

    public WebDriver getLocalWebDriver(@NonNull String browserName, boolean enableLogging) {
        String path = ConstantsProvider.getSELENIUM_DRIVER_PATH();
        WebDriver webDriver;
        DesiredCapabilities capabilities = getDesiredCapabilities(browserName, enableLogging);
        ChromeOptions chromeOptions = getChromeOptions(browserName);
        switch (browserName.toLowerCase()) {
        case "chrome-headless":
            //ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("headless");
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        case "chrome":
            path += "/chrome/chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", path);
            //webDriver = new ChromeDriver(capabilities);
            //webDriver = new ChromeDriver(chromeOptions);
            webDriver = new ChromeDriver();
            break;
        case "ie":
            path += "/ie/IEDriverServer-32-3.9.0.exe";
            System.setProperty("webdriver.ie.driver", path);
            log.debug("Path " + path);
            capabilities = new DesiredCapabilities(); //DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank");
            capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
            capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,UnexpectedAlertBehaviour.ACCEPT);
            capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
            capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, false);
            capabilities.setCapability(InternetExplorerDriver.BROWSER_ATTACH_TIMEOUT, 10000);
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, false);
            //webDriver = new InternetExplorerDriver(capabilities);
            webDriver = new InternetExplorerDriver();
            break;
        case "firefox":
            path += "/firefox/geckodriver-64bits.exe";
            System.setProperty("webdriver.gecko.driver", path);
            FirefoxOptions ffOptions = new FirefoxOptions();
            webDriver = new FirefoxDriver(ffOptions);
            break;
        default:
            throw new IllegalArgumentException("Browser: " + browserName + " not " + "supported..");
        }
        return webDriver;
    }

    /*
     * To return chrome Options
     * */
    public static ChromeOptions getChromeOptions(@NonNull String browserName)
    {
    	ChromeOptions chromeOptions = new ChromeOptions();
        switch (browserName.toLowerCase()) {
        case "chrome-headless":
        case "chrome":
            chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
            chromeOptions.addArguments("disable-extensions", "--start-maximized");
            chromeOptions.addArguments("--incognito");
            chromeOptions.setExperimentalOption("w3c", false);
            chromeOptions.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            chromeOptions.setCapability("chrome.switches", Arrays.asList("--auth-server-whitelist='jira.*.agriculture.gov.ie'")); // "--incognito",
            break;
        }
        
        return chromeOptions;
    	
    }
    
    
    
    private static DesiredCapabilities getDesiredCapabilities(@NonNull String browserName) {
        DesiredCapabilities capabilities;
        ChromeOptions chromeOptions = new ChromeOptions();
        switch (browserName.toLowerCase()) {
        case "chrome-headless":
        case "chrome":
            capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
            chromeOptions.addArguments("disable-extensions", "--start-maximized");
            chromeOptions.addArguments("--incognito");
            chromeOptions.setExperimentalOption("w3c", false);
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            capabilities.setCapability("chrome.switches", Arrays.asList("--auth-server-whitelist='jira.*.agriculture.gov.ie'")); // "--incognito",
            break;

        case "ie":
            capabilities = new DesiredCapabilities();// DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank");
            capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
            capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
            capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
            capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, false);
            capabilities.setCapability(InternetExplorerDriver.BROWSER_ATTACH_TIMEOUT, 10);
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            break;

        case "firefox":
            FirefoxOptions ffOptions = new FirefoxOptions();
            capabilities = new DesiredCapabilities();
            FirefoxProfile profile = new FirefoxProfile(); // new ProfilesIni().getProfile("default");
            File profileDir = profile.layoutOnDisk();
            profile.clean(profileDir);
            profile.deleteExtensionsCacheIfItExists(profileDir);
            profile.setAcceptUntrustedCertificates(true);
            profile.setAssumeUntrustedCertificateIssuer(false);
            profile.setPreference("network.proxy.type", 2);
            ffOptions.setCapability("marionette", true);
            ffOptions.addArguments(Arrays.asList("-private"));
            ffOptions.setProfile(profile);
            capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, ffOptions);
            break;

        default:
            throw new IllegalArgumentException("Browser: " + browserName + " not " + "supported..");
        }
        return capabilities;
    }

    public static DesiredCapabilities getDesiredCapabilities(@NonNull String browserName,        boolean enableLogging) {
        DesiredCapabilities capabilities = getDesiredCapabilities(browserName);
        if (capabilities != null && enableLogging) {
            log.debug("Enable logging on browser of type performance");
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        }
        return capabilities;
    }

    private class WebEventListener extends AbstractWebDriverEventListener {

        @Override
        public void onException(Throwable error, WebDriver driver) {
            if (error instanceof UnhandledAlertException) {
                String msg = "";
                if (driver != null) {
                    msg = "while browser open ";
                    String currentUrl = driver.getCurrentUrl();
                    if (!Strings.isNullOrEmpty(currentUrl)) {
                        msg = msg + "on URL: " + currentUrl;
                    }
                    String title = driver.getTitle();
                    if (!Strings.isNullOrEmpty(title)) {
                        msg = msg + " While on page with title " + title;
                    }
                    String alertText = ((UnhandledAlertException) error).getAlertText();
                    msg = msg + " Alert text: " + alertText;
                }
                throw new UnhandledAlertException(msg);
            }
        }
    }

}
