package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import core.BaseUtils.common;
import io.restassured.path.json.JsonPath;

/**
 * This Base Class is created and maintained by-@author Faiz-Siddiqh
 */
public class BaseUtils {

	private static Properties properties = new Properties();
	private static ExtentReports extentreport;
	private static ExtentTest test;
	public static String methodName;
	public static String moduleName;
	public static WebDriver driver;

	public static class ProjectProperties {

		/**
		 * To Load a properties file
		 * 
		 * @param filePath-Path of the properties file
		 */
		public static void loadPropertiesFile(String filePath) {
			try {
				properties = new Properties();
				properties.load(new FileReader(System.getProperty("user.dir") + filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Read the properties of a particular module
		 * 
		 * @param propertyName
		 * @return value of the particular property
		 */
		public static String readProjectVariables(String propertyName) {
			properties = new Properties();
			loadPropertiesFile("\\ExecutionFiles\\" + moduleName + "\\module.properties"); // Here the module name has
																							// to be
			// specified manually.Yet to update
			// this method.
			return properties.getProperty(propertyName);

		}

		/**
		 * To read a property from config properties file
		 * 
		 * @param propertyName
		 * @return property value from golbal config value
		 */
		public static String readFromGlobalConfigFile(String propertyName) {
			loadPropertiesFile("//Resources//config.properties");
			return properties.getProperty(propertyName);

		}

	}

	public static class common {

		/**
		 * To set name of the method currently in execution.This method is necessary to
		 * start a new ExtentTest
		 * 
		 * @param methodName
		 */
		public static void setMethodName(String methodName) {
			BaseUtils.methodName = methodName;
		}

		/**
		 * To set name of the module currently in execution.This method is necessary to
		 * get module properties
		 * 
		 * @param methodName
		 */
		public static void setModuleName(String moduleName) {
			BaseUtils.moduleName = moduleName;
		}

		/**
		 * Navigate to the Url
		 * 
		 * @param url
		 */
		public static void navigateToUrl(String url) {
			driver.get(url);
			common.logInfo("Navigating to -" + url);

		}

		/**
		 * Return an instance of WebDriver Object
		 * 
		 * @return WebDriver
		 */
		public static WebDriver getWebDriver() {
			return driver;
		}

		/**
		 * To create a new instance of Extent report.
		 */
		public static void getExtentReportInstance() {

			String path = System.getProperty("user.dir") + "//TestResults//" + moduleName;
			File resultsFile = new File(path);
			// if the extent report already exists delete.else create a new directory of
			// that
			// module
			if (resultsFile.exists()) {
				resultsFile.delete();
			}
			resultsFile.mkdir();
			extentreport = new ExtentReports(path + "//" + "ExtentReport.html", false);// to create a new extent report
																						// for every module ,change to
																						// true.
			extentreport.addSystemInfo("Selenium Version", "3.141.59").addSystemInfo("Platform", "Windows");
			// extent.addSystemInfo("Selenium Version",
			// "3.141.59").addSystemInfo("Platform", System.getProperty("os.name"));

		}

		/**
		 * return an instance of extent report
		 * 
		 * @return ExtentReport
		 */
		public static ExtentReports getExtentReport() {
			return extentreport;
		}

		/**
		 * Start an extent test report
		 * 
		 * @param testName
		 */

		public static void setExtentTest(String testName) {
			setMethodName(testName);
			test = extentreport.startTest(testName);
			test.log(LogStatus.INFO, "Setting log report");
			test.log(LogStatus.INFO, "Starting Test-" + testName);

		}

		/**
		 * Returns an instance of ExtentTest
		 * 
		 * @return extentTest
		 */
		public static ExtentTest getExtentTest() {
			return test;
		}

		/**
		 * Logs the information to the extent report
		 * 
		 * @param log-Info to log into the extent report
		 */

		public static void logInfo(String log) {
			test.log(LogStatus.INFO, log);
		}

		/**
		 * CleanUp after Successful run of a testcase
		 */
		public static void cleanUpOnSuccess() {
			test.log(LogStatus.PASS, "Test Passed Successfully");
			BaseUtils.common.getExtentReport().endTest(test);
			BaseUtils.common.getExtentReport().flush();

		}

		/**
		 * CleanUp after Testcase fails
		 */
		public static void cleanUpOnFailure() {
			test.log(LogStatus.FAIL, "Test Failed ");
			BaseUtils.common.getExtentReport().endTest(test);
			BaseUtils.common.getExtentReport().flush();

		}

	}

	public static class locators {
		private static Document doc;
		private static XPath xpath;
		private static XPathExpression expr;

		/**
		 * Set up the locators file for entire project. [MODULE SPECIFIC LOCATORS SETUP
		 * IS YET TO BE IMPLEMENTED]
		 * 
		 * @param moduleLocatorFileName
		 */
		public static void setUpLocatorsFile() {

			// READING THE PATH OF LOCATORS FILE FROM MODULE LOCATOR FILE
			String locatorsFileLocation = ProjectProperties.readFromGlobalConfigFile("locators");
			File file = new File(
					System.getProperty("user.dir") + locatorsFileLocation + moduleName + "//" + moduleName + ".xml");

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = documentFactory.newDocumentBuilder();
				doc = builder.parse(file);
				XPathFactory xpathFactory = XPathFactory.newInstance();
				xpath = xpathFactory.newXPath();
			} catch (ParserConfigurationException e) {
				System.out.println("Xml file parsing failed");
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * 
		 * @param locatorname
		 * @return locator value of that unique specified locator name passed.
		 */
		public static String getLocator(String locatorname) {
			String locator = null;
			try {
				expr = xpath.compile("//element[@name='" + locatorname + "']/@*");
				NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				common.logInfo("Get Locator for " + locatorname);
				Attr attr = (Attr) result.item(0);
				locator = attr.getNodeValue();
				common.logInfo("Get Locator successful- " + locator);
				// return attr.getTextContent();

			} catch (XPathExpressionException e) {
				// System.out.println("check the locatorname input value");
				common.logInfo("Get Locator unsuccessful- " + locator);
				// common.logInfo(e.getMessage());
			}

			return locator;
		}

	}

	public static class testData {
		public static XSSFWorkbook ExcelWBook;
		private static XSSFSheet ExcelWSheet;
		public static String filePath;

		/**
		 * To set up the test file from which the testdata has to be read.
		 * 
		 * @param fileName
		 */
		public static void setTestFile(String fileName) {
			try {
				// Open the Excel file
				filePath = System.getProperty("user.dir") + "//ExecutionFiles//" + moduleName + "//" + fileName
						+ ".xlsx";
				FileInputStream ExcelFile = new FileInputStream(filePath);

				// Access the excel data sheet
				ExcelWBook = new XSSFWorkbook(ExcelFile);
				// SHEET NAME TO TESTDATA IS SAME FOR ALL THE MODULES
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 
		 * @return the testdata file
		 */
		public static XSSFWorkbook getExcelWorkBook() {

			return ExcelWBook;
		}

		/**
		 * To fetch the testdata from the Excelfile .PLEASE REFER THE TESTDATA FILE ON
		 * THE COLUMN VALUE OF VARIABLENAME,VARIABLE VALUE
		 * 
		 * @param testVariable
		 * @return testdata for the specific value passed
		 */
		public static String getTestData(String testVariable) {
			try {

				ExcelWSheet = ExcelWBook.getSheet("TestData");
				// LOOPING THROUGH ALL THE ROWS OF THE EXCELSHEET
				for (org.apache.poi.ss.usermodel.Row eachRow : ExcelWSheet) {

					XSSFCell Cell = (XSSFCell) eachRow.getCell(4); // GET CELL WHICH HAS METHOD NAME
					XSSFCell variableCell = (XSSFCell) eachRow.getCell(5);// GET CELL WHICH HAS VARIABLE NAME
					XSSFCell variableValueCell = (XSSFCell) eachRow.getCell(6);// GET CELL WHICH HAS VARIABLE VALUE
					// The value is fetched only if the current method name and variable name
					// matches the value in the cell
					if (Cell.getStringCellValue().equals(methodName)
							&& variableCell.getStringCellValue().equals(testVariable)) {
						common.logInfo("LookUp for testdata -" + testVariable);
						if (variableValueCell.getCellType() == CellType.STRING) {
							common.logInfo("LookUp for testdata " + testVariable + " successful.value = "
									+ variableValueCell.getStringCellValue());

							return variableValueCell.getStringCellValue();

						} else if (variableValueCell.getCellType() == CellType.NUMERIC) {

							common.logInfo("LookUp for testdata " + testVariable + " successful.value = "
									+ String.valueOf(variableValueCell.getNumericCellValue()));
							return String.valueOf(variableValueCell.getNumericCellValue());
						}

						// THE TESTDATA FOR CELL TYPE OTHER THAN STRING OR NUMERIC HAS TO BE IMPLEMENTED
					}

				}
				common.logInfo("LookUp for testdata failed.Testdata not found");

			} catch (Exception e) {
				common.logInfo("LookUp for testdata failed.");
				common.logInfo(e.getMessage());
			}
			return null;

		}

		/**
		 * To fetch the Global Variables from the Excelfile .PLEASE REFER THE
		 * GlOBALVARIABLEs SHEET in Testdata FILE THE COLUMN VALUE OF
		 * VARIABLENAME,VARIABLE VALUE
		 * 
		 * @param testVariable
		 * @return testdata for the specific value passed
		 */
		public static String getGlobalVariablesTestdata(String testVariable) {
			try {

				ExcelWSheet = ExcelWBook.getSheet("GlobalVariables");
				// LOOPING THROUGH ALL THE ROWS OF THE EXCELSHEET
				for (org.apache.poi.ss.usermodel.Row eachRow : ExcelWSheet) {

					XSSFCell variableCell = (XSSFCell) eachRow.getCell(1);// GET CELL WHICH HAS VARIABLE NAME
					XSSFCell variableValueCell = (XSSFCell) eachRow.getCell(2);// GET CELL WHICH HAS VARIABLE VALUE

					// The value is fetched only if the current method name and variable name
					// matches the value in the cell
					if (variableCell.getStringCellValue().equals(testVariable)) {
						common.logInfo("LookUp for testdata -" + testVariable);

						if (variableValueCell.getCellType() == CellType.STRING) {
							common.logInfo("LookUp for testdata " + testVariable + " successful.value = "
									+ variableValueCell.getStringCellValue());

							return variableValueCell.getStringCellValue();

						} else if (variableValueCell.getCellType() == CellType.NUMERIC) {

							common.logInfo("LookUp for testdata " + testVariable + " successful.value = "
									+ String.valueOf(variableValueCell.getNumericCellValue()));
							return String.valueOf(variableValueCell.getNumericCellValue());
						}

						// THE TESTDATA FOR CELL TYPE OTHER THAN STRING OR NUMERIC HAS TO BE IMPLEMENTED
					}

				}
				common.logInfo("LookUp for testdata failed.Testdata not found");

			} catch (Exception e) {
				common.logInfo("LookUp for testdata failed.");
				common.logInfo(e.getMessage());
			}
			return null;

		}

	}

	/**
	 * SetUp the WEBDRIVER of the type specified in the config file . IMPLEMNETED
	 * FOR CHROME AND FIREFOX BROWSERS ONLY AND FOR BOTH MAC OS AND WINDOWS
	 */
	public static void setUpDriver() {
		// Load the properties file using this method which contains baseURL and
		// WebDriverType
		String driverLocation;
		common.logInfo("Setting Up WebDriver");
		String driverName = ProjectProperties.readFromGlobalConfigFile("driver");
		// String baseURL = projectDetails.getProperty("baseURL");
		common.logInfo("WebDriver chosen =" + driverName);

		if (driverName.equalsIgnoreCase("Chrome")) {
			// Set System Property to instantiate ChromeDriver with the path of
			// chromedriver.

			driverLocation = ProjectProperties.readFromGlobalConfigFile("chromedriver");
			if (System.getProperty("os.name").startsWith("Windows")) {// the path varies for windows and Mac
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + driverLocation);
			} else {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + driverLocation.replaceAll(".exe", ""));
			}
			// Set Options using for chrome using the below commented line

			ChromeOptions options = new ChromeOptions();
			// options.addArguments("--headless");
			driver = new ChromeDriver(options);
			common.logInfo("Launching Chrome");

		} else if (driverName.equalsIgnoreCase("FireFox")) {
			// Set System Property to instantiate ChromeDriver with the path of
			// firefoxdriver.
			driverLocation = ProjectProperties.readFromGlobalConfigFile("firefoxdriver");

			if (System.getProperty("os.name").startsWith("Windows")) {// the path varies for windows and Mac
				System.setProperty("webdriver.firefox.driver", System.getProperty("user.dir") + driverLocation);
			} else {
				System.setProperty("webdriver.firefox.driver",
						System.getProperty("user.dir") + driverLocation.replaceAll(".exe", ""));
			}

			// Set Options using for Firefox

			org.openqa.selenium.firefox.ProfilesIni profile = new org.openqa.selenium.firefox.ProfilesIni();
//			FirefoxProfile Automationprofile = profile.getProfile("Automation");// Create a profile with Automation in
//																				// Firefox on
//																				// your machine and login your cognizant
//																				// credentials
			FirefoxOptions options = new FirefoxOptions();
//			options.setProfile(Automationprofile);
			driver = new FirefoxDriver(options);
			common.logInfo("Launching Firefox");
		}

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		common.logInfo("Maximizing the window");

	}

	/**
	 * Initial SETUP of the module -before class/suite .
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void setUp(String moduleName) {
		common.setModuleName(moduleName);
		// setting up an extent report
		common.getExtentReportInstance(); // setting up an extent report
		/*
		 * Setting up Locators File
		 */
		locators.setUpLocatorsFile();
		testData.setTestFile(moduleName);

	}

	/**
	 * Takes in a json response and returns the value to be extracted from the
	 * response
	 * 
	 * @param response
	 * @param valueToBeExtracted
	 * @return
	 */
	public static String extractFromJson(String response, String valueToBeExtracted) {

		JsonPath jp = new JsonPath(response);// for parsing json
		String value = jp.get(valueToBeExtracted);
		return value;
	}

	/**
	 * Wait for the page to load completely
	 */
	public static void waitForThePageToLoad() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					common.logInfo("Waiting for page to Load Completely.");
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}
			});
			// driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);

		} catch (Exception e) {
			common.logInfo("WebPage took more time to Load.");
			driver.quit();
		}
	}

	/**
	 * Find Element by locator and type
	 * 
	 * @param locator
	 * @param type
	 * @return WebElement
	 */
	public static WebElement getElement(String locator, String type) {
		WebElement element = null;
		type = type.toLowerCase();
		common.logInfo("Lookup for Element-" + locator);
		try {
			if (type.equals("id")) {
				element = driver.findElement(By.id(locator));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(locator));
			} else if (type.equals("cssselector")) {
				element = driver.findElement(By.cssSelector(locator));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(locator));
			} else if (type.equals("classname")) {
				element = driver.findElement(By.className(locator));
			} else if (type.equals("tagname")) {
				element = driver.findElement(By.tagName(locator));
			} else if (type.equals("linktext")) {
				element = driver.findElement(By.linkText(locator));
			}
		} catch (Exception e) {
			common.logInfo("Element not found -" + locator);
			common.logInfo("Locator not supported or check type");

		}
		common.logInfo("Lookup for Element successful");
		return element;
	}

	/**
	 * Click And Wait on specific WebElement and Log the message in extent report
	 * 
	 * @param element
	 * @param message
	 */
	public static void clickAndWait(WebElement element, String message) {
		try {
			waitForTheElementToBeClickable(10, element);
			element.click();
			Thread.sleep(4000);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			common.logInfo(message);

		} catch (Exception e) {
			common.logInfo("Element not clickable");
			common.logInfo(e.getMessage());

		}

	}

	/**
	 * wait for the element to be clickable.**
	 * 
	 * @param timeOutInSeconds
	 * @param element
	 */
	public static void waitForTheElementToBeClickable(long timeOutInSeconds, WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			common.logInfo("Waiting for the element to be clickable");
		} catch (Exception e) {
			common.logInfo("Element not clickable OR available");
			common.logInfo(e.getMessage());
		}
	}

	/**
	 * Click And Wait on the WebElement
	 * 
	 * @param element
	 */
	public static void clickAndWait(WebElement element) {
		try {
			element.click();
			Thread.sleep(3000);
			common.logInfo("Click and Wait");
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		} catch (Exception e) {
			common.logInfo(e.getMessage());
			driver.quit();
//			extentreport.endTest(test);
//			extentreport.flush();
		}

	}

	/**
	 * Click And Wait on the specified WebELement & Type and Wait the Keys .Log the
	 * message to the report
	 * 
	 * @param element
	 * @param keysToSend
	 * @param message
	 */
	public static void clickAndTypeAndWait(WebElement element, String keysToSend, String message) {
		try {
			clickAndWait(element);
			element.sendKeys(keysToSend);
			common.logInfo(message);
			Thread.sleep(3000);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			common.logInfo(e.getMessage());
		}
	}

	public static void waitToPerformAction(long timeToWait,String Message) {
		try {
			common.logInfo("Waiting for " + timeToWait + " Seconds");
			Thread.sleep(timeToWait);
			common.logInfo(Message);

		} catch (InterruptedException e) {
			common.logInfo("Exception/error during wait");
			e.printStackTrace();
		}

	}

	/**
	 * To arrange the values in a map in an ascending order[Including duplicate
	 * Keys]and return a Sorted Set
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return A SortedSet of sorted Map based on its Value of a entry
	 */
	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

}