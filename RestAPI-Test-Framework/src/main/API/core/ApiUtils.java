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

import core.ApiUtils.common;
import io.restassured.path.json.JsonPath;

/**
 * This Base Class is created and maintained by-
 * 
 * @author Faiz-Siddiqh
 */
public class ApiUtils {

	private static Properties properties = new Properties();
	private static ExtentReports extentreport;
	private static ExtentTest test;
	public static String methodName;
	public static String moduleName;

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
		 * @return property value from global config value
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
			ApiUtils.methodName = methodName;
		}

		/**
		 * To set name of the module currently in execution.This method is necessary to
		 * get module properties
		 * 
		 * @param methodName
		 */
		public static void setModuleName(String moduleName) {
			ApiUtils.moduleName = moduleName;
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
			ApiUtils.common.getExtentReport().endTest(test);
			ApiUtils.common.getExtentReport().flush();

		}

		/**
		 * CleanUp after Testcase fails
		 */
		public static void cleanUpOnFailure() {
			test.log(LogStatus.FAIL, "Test Failed ");
			ApiUtils.common.getExtentReport().endTest(test);
			ApiUtils.common.getExtentReport().flush();

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
		public static void setTestFile() {
			try {
				// Open the Excel file
				filePath = System.getProperty("user.dir") + "//ExecutionFiles//" + moduleName + "//" + moduleName
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

					XSSFCell Cell = (XSSFCell) eachRow.getCell(2); // GET CELL WHICH HAS METHOD NAME
					XSSFCell variableCell = (XSSFCell) eachRow.getCell(3);// GET CELL WHICH HAS VARIABLE NAME

					// The value is fetched only if the current method name and variable name
					// matches the value in the cell
					if (Cell.getStringCellValue().equals(methodName)
							&& variableCell.getStringCellValue().equals(testVariable)) {
						XSSFCell variableValueCell = (XSSFCell) eachRow.getCell(4);// GET CELL WHICH HAS VARIABLE VALUE
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
		common.setExtentTest("AddPlace");
		testData.setTestFile();

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