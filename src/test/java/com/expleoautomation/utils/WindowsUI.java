package com.expleoautomation.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Application;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Button;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Hyperlink;
import mmarquee.automation.controls.ImplementsClick;
import mmarquee.automation.controls.MenuItem;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.Search;
import mmarquee.automation.controls.SystemMenu;
import mmarquee.automation.controls.TextBox;
import mmarquee.automation.controls.Window;
import mmarquee.automation.pattern.PatternNotFoundException;

@Log4j2
public class WindowsUI {

	StopWatch timeout = new StopWatch();
	Application app = null;
	Window win = null;

	public void open(String path) {
		app = launchApplication(path);

		win = getWindow("Chrome_WidgetWin_1");
		Button btnKeep = getButton("Keep");
		click(btnKeep);
		Button btnFrmservlet =  getButton(" frmservlet.jnlp");
		click(btnFrmservlet);
		
		
		win = getWindow("Chrome_WidgetWin_1");
		Hyperlink hLogout = getHyperlink("Logout");
		click(hLogout);
		close();
		
		win = getWindow("Oracle Fusion Middleware Forms Services");
		
		
		SystemMenu menu = getSystemMenu();
//		menu.getItem(path);
		try {
			List<MenuItem> items = menu.getItems();
			items.size();
			items.get(0).click();
			
			help(true);
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		help(true);


		
		win.focus();

		

	}

	public Application launchApplication(String path) {
		log.info("launchApplication() " + path);
		timeout.reset();
		timeout.start();
		UIAutomation ui = UIAutomation.getInstance();
		Application newApplication = null;
		try {
			newApplication = ui.launchOrAttach(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (newApplication == null) {
			log.error("FAILED to launch application '" + path + "'");
		} else {
			log.info("FOUND " + path + " duration: " + timeout.getTime() + "ms");
		}
		return newApplication;
	}

	public Window getWindow(String name) {
		log.info("getWindow() " + name);
		timeout.reset();
		timeout.start();
		Window newWindow = null;
		while (newWindow == null && !isTimeout()) {
			if (win != null && newWindow == null) {
				try {
					newWindow = win.getWindow(name);
				} catch (AutomationException e) {}
			}
			if (newWindow == null) {

				try {
					newWindow = app.getWindow(name);
				} catch (AutomationException e) {}
			}
			if (newWindow == null) {
				try {
					newWindow = app.getWindowByClassName(name);
				} catch (AutomationException e) {}
			}

		}
		if (newWindow == null) {
			String msg = "FAILED to find window '" + name + "'";
			log.error(msg);
			//try {
				//help("", app.getChildren(true));
			//} catch (PatternNotFoundException e) {	
			//} catch (AutomationException e) {	}
			Assert.fail(msg);

		} else {
			log.info("FOUND " + name + " duration: " + timeout.getTime() + "ms");
		}

		/*
		 * try { log.info(newWindow.getName());
		 * log.info(newWindow.getAutomation().getRootElement().getAutomationId()); }
		 * catch (AutomationException e2) {}
		 */
		return newWindow;
	}
	
	
	

	public SystemMenu getSystemMenu() {
		log.info("getSystemMenu()");
		timeout.reset();
		timeout.start();
		SystemMenu newMenu = null;
		while (newMenu == null && !isTimeout()) {
			try {
				
				//getName:System	getAutomationId:MenuBar
				newMenu = win.getSystemMenu();
			} catch (AutomationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (newMenu == null) {
			String msg = "FAILED to find window 'SystemMenu'";
			log.error(msg);
			//try {
				//help("", app.getChildren(true));
			//} catch (PatternNotFoundException e) {	
			//} catch (AutomationException e) {	}
			Assert.fail(msg);

		} else {
			log.info("FOUND 'SystemMenu' duration: " + timeout.getTime() + "ms");
		}

		/*
		 * try { log.info(newWindow.getName());
		 * log.info(newWindow.getAutomation().getRootElement().getAutomationId()); }
		 * catch (AutomationException e2) {}
		 */
		return newMenu;
	}

	


	
	public Button getButton(String name) {
		log.info("getButton() " + name);
		timeout.reset();
		timeout.start();
		Button newButton = null;
		while (newButton == null && !isTimeout()) {

			// by name
			try {
				newButton = win.getButton(Search.getBuilder().name(name).build());
			} catch (AutomationException e) {
				Thread.yield();
			}

			// by automation ID
			if (newButton == null) {
				try {
					newButton = win.getButton(Search.getBuilder().automationId(name).build());
				} catch (AutomationException e) {
				}
			}		}
		if (newButton == null) {
			String msg = "FAILED to find button '" + name + "'";
			log.error(msg);
			Assert.fail(msg);
		} else {
			log.info("FOUND " + name + " duration: " + timeout.getTime() + "ms");
		}

		return newButton;
	}

	public Hyperlink getHyperlink(String name) {
		log.info("getHyperlink() " + name);
		timeout.reset();
		timeout.start();
		Hyperlink newtHyperlink = null;
		while (newtHyperlink == null && !isTimeout()) {

			// by name
			try {
				newtHyperlink = win.getHyperlink(Search.getBuilder().name(name).build());
			} catch (AutomationException e) {
				Thread.yield();
			}

			// by automation ID
			if (newtHyperlink == null) {
				try {
					newtHyperlink = win.getHyperlink(Search.getBuilder().automationId(name).build());
				} catch (AutomationException e) {
				}
			}		}
		if (newtHyperlink == null) {
			String msg = "FAILED to find button '" + name + "'";
			log.error(msg);
			Assert.fail(msg);
		} else {
			log.info("FOUND " + name + " duration: " + timeout.getTime() + "ms");
		}

		return newtHyperlink;
	}

	public Panel getPanel(String name) {
		log.info("getPanel() " + name);
		timeout.reset();
		timeout.start();
		Panel newPanel = null;
		while (newPanel == null && !isTimeout()) {

			// by name
			try {
				newPanel = win.getPanel(Search.getBuilder().name(name).build());
			} catch (AutomationException e) {
				Thread.yield();
			}

			// by automation ID
			if (newPanel == null) {
				try {
					newPanel = win.getPanel(Search.getBuilder().automationId(name).build());
				} catch (AutomationException e) {
				}
			}

			

		}
		if (newPanel == null) {
			String msg = "FAILED to find panel '" + name + "'";
			log.error(msg);
			Assert.fail(msg);
		} else {
			log.info("FOUND " + name + " duration:" + timeout.getTime() + "ms");
		}

		return newPanel;
	}

	public TextBox getTextBox(String automationID) {
		log.info("getTextBox() " + automationID);
		timeout.reset();
		timeout.start();
		TextBox textBox = null;
		while (textBox == null && !isTimeout()) {
			try {
				textBox = win.getTextBox(Search.getBuilder().automationId(automationID).build());
			} catch (AutomationException e) {
			}
		}
		if (textBox == null) {
			String msg = "FAILED to find text-box '" + automationID + "'";
			log.error(msg);
			Assert.fail(msg);
		} else {
			log.info("FOUND " + automationID + " duration: " + timeout.getTime() + "ms");
		}
		return textBox;
	}

	private boolean isTimeout() {
		return timeout.getTime() > 30 * 1000; // 5s
	}

	public static String getClipboard() {
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data = null;
		try {
			data = clpbrd.getData(DataFlavor.stringFlavor).toString();
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public void click(ImplementsClick btn) {

		try {
			btn.click();
		} catch (PatternNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (AutomationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void wait(int milliseconds) {
		try {
			win.wait(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void keyPress() {
		Robot robot;
		try {
			robot = new Robot();
			// robot.setAutoDelay(250);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyPress(KeyEvent.VK_ENTER);

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		// close window
		try {
			win.close();
		} catch (PatternNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void help(boolean deep) {
			
			try {
				
				help("	", app.getChildren(deep), "SunAwtFrame", deep);
			} catch (PatternNotFoundException e) { 
			} catch (AutomationException e) { }
	}

	public void help(String indent, List<AutomationBase> children, String filter, boolean deep) {

		for (AutomationBase child: children) {
			try {
				if (filter == null) {
					log.info(indent + "toString:"+child.toString() + "	getClassName:" +child.getClassName() + "	getName:" + child.getName() + "	getAutomationId:" + child.getElement().getAutomationId());
					help(indent+ "	", child.getChildren(deep), null, deep);
				} else if (child.getClassName().contains(filter)) {
					log.info(indent + child.getDescription() + "	" + child.getClassName() + "	" + child.getName() + "	" + child.getElement().getAutomationId() + "	"  + child.getElement().getRuntimeId());
					help(indent+ "	", child.getChildren(deep), null, deep);					
				}
			} catch (AutomationException e) { }
		}		
	}

	@Test
	public void myTest() {

		// https://www.autoitscript.com/forum/topic/179305-how-to-get-the-code-from-the-vip-access/

		log.info("Get VIP security code");

		StopWatch timer = new StopWatch();
		timer.start();

		app = launchApplication("C:\\Program Files (x86)\\Symantec\\VIP Access Client\\VIPUIManager.exe");
		win = getWindow(" VIP Access");

		// displayWindowControls(win);

		try {
			String credentials = getTextBox("11001").getName();
			String code = getTextBox("11007").getName();

			log.info("* " + credentials + " *");
			log.info("* " + code + " *");

		} catch (AutomationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.info(timer.getTime() + "ms");

		close();

	}

}

/*
 * // by index newButton = win.getButton(Search.getBuilder(0).build());
 * newButton = win.getButton(0);
 * 
 * // by name newButton =
 * win.getButton(Search.getBuilder("Copy Security code").build()); newButton =
 * win.getButton("Copy Security code");
 * 
 * // by automationID newButton =
 * win.getButton(Search.getBuilder().automationId(name).build()); newButton =
 * win.getButtonByAutomationId(name);
 * 
 * // by class-name / control-type //newButton =
 * win.getButton(Search.getBuilder().className("Button").controlType("50000").
 * build());
 * 
 * log.info("   getAutomationId() " + newButton.getElement().getAutomationId());
 * log.info("   getName() " + newButton.getElement().getName());
 * log.info("   getClassName() " + newButton.getElement().getClassName());
 * log.info("   getControlType() " + newButton.getElement().getControlType());
 */
