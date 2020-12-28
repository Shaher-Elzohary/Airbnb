package com.google.selenium.test;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

public class AirbnbSearchValidation {

	static ChromeDriver chrome;
	static WebDriverWait wait;
	JavascriptExecutor js;
	String endDay = "";
	String startDay = "";
	String location = "";

	@BeforeTest
	public void beforeTest() {
		//Create Options to stop chrome popup and notification
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-notifications");
		//set the Chrome driver
		System.setProperty("webdriver.chrome.driver", 
				System.getProperty("user.dir") + "\\resources\\chromedriver.exe");
		chrome = new ChromeDriver(options);
		//create JS for srolling up or down
		js = (JavascriptExecutor) chrome;
		//set wiating time for loading any element 60sec
		wait = new WebDriverWait(chrome, 60);
		//set Chrome window to max and go to airbnb
		chrome.manage().window().maximize();
		chrome.navigate().to("http://airbnb.com.");
		//check airbnb opened correct with the correct title
		Assert.assertEquals(chrome.getTitle(), "Vacation Rentals, Homes, Experiences & Places - Airbnb");
	}

	@AfterTest(enabled = true)
	public void afterTest() {
		//close Chrome driver after the test
		chrome.quit();
	}
	
	@DataProvider()
	public Object[][] dp() {
		//using data driven for the check in/out and loaction
		ReadExcelFile excelFile = new ReadExcelFile(System.getProperty("user.dir")+"\\src\\main\\UserData.xlsx");
		return new Object[][] {
			new Object[] { excelFile.getData(0, 1, 0), excelFile.getData(0, 1, 1), excelFile.getData(0, 1, 2)},
		};
	}

	@Test(dataProvider = "dp", priority = 0, enabled = true)
	public void search(String location, String checkInAfter, String checkOutAfter)
	{
		//send location to start search
		this.location = location;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[1]/div/div/label"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"bigsearch-query-detached-query\"]"))).sendKeys(location);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"bigsearch-query-detached-query\"]"))).sendKeys(Keys.RETURN);
		//get the current day according to the time zone
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int currentMonth = calendar.get(calendar.MONTH);
		//add 7 days to the current day
		calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(checkInAfter));		
		int todayInt = calendar.get(calendar.DAY_OF_MONTH);		
		//convert the day number to string
		startDay = Integer.toString(todayInt);
		/*
		 * Due to the mazimize of the window we will have two months viewed in check in/out
		 * so we have to calculate if the day selected in the 1st or 2nd month viewed
		 * ex: if we are 30.12.2020 so we have to add 7 days that will result 5.1.2021 so we have to select 5 in the second viewed month
		 * 
		 */
		//get all available days in the month and check after adding the 7 days for check in, if it will be still in the same month or in the next month
		if(chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[1]/div")).getAttribute("aria-expanded").equals("true")) {
			if (currentMonth == calendar.get(calendar.MONTH)){
				WebElement dateWidgetFrom = chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[4]/section/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[2]/div/table/tbody"));
				List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));
		        for (WebElement cell: columns) {
		            if (cell.getText().equals(startDay)) {
		                cell.click();
		                break;
		            }
		        }
		        //after selecting the check in day in the first month check if the check out date will be in the first month or in the second month
		        if(chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[3]/div")).getAttribute("aria-expanded").equals("true")) {	
					currentMonth = calendar.get(calendar.MONTH);
					calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(checkOutAfter));
					todayInt = calendar.get(calendar.DAY_OF_MONTH);
					startDay = Integer.toString(todayInt);
					if (currentMonth == calendar.get(calendar.MONTH)){
						dateWidgetFrom = chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[4]/section/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[2]/div/table/tbody"));
						columns = dateWidgetFrom.findElements(By.tagName("td"));
				        for (WebElement cell: columns) {
				            if (cell.getText().equals(startDay)) {
				                cell.click();
				                break;
				            }
				        }
					}else {
						dateWidgetFrom = chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[4]/section/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[3]/div/table/tbody"));
						columns = dateWidgetFrom.findElements(By.tagName("td"));
				        for (WebElement cell: columns) {
				            if (cell.getText().equals(startDay)) {
				                cell.click();
				                break;
				            }
				        }
					}
		        }
			}else {
				//if the check in is in the second month so for sure the check out will be in the second month
				WebElement dateWidgetFrom = chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[4]/section/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[3]/div/table/tbody"));
				List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));
		        for (WebElement cell: columns) {
		            if (cell.getText().equals(startDay)) {
		                cell.click();
		                break;
		            }
		        }
		        //after selecting check in day in the second month the select the check out day in the second month also
		        if(chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[3]/div")).getAttribute("aria-expanded").equals("true")) {
					calendar.add(Calendar.DAY_OF_MONTH, 7);
					todayInt = calendar.get(calendar.DAY_OF_MONTH);
					endDay = Integer.toString(todayInt);
						dateWidgetFrom = chrome.findElement(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[3]/div[4]/section/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[3]/div/table/tbody"));
						columns = dateWidgetFrom.findElements(By.tagName("td"));
				        for (WebElement cell: columns) {
				            if (cell.getText().equals(endDay)) {
				                cell.click();
				                break;
				            }
				        }
					}
				}
			}
		//select the number of guests fore the search
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[5]/div[1]/div"))).click();
		//add two adults
		if (wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchFlow-title-label-stepper-adults"))).isDisplayed()) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"stepper-adults\"]/button[2]"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"stepper-adults\"]/button[2]"))).click();
		}
		//add one children
		if (wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchFlow-title-label-stepper-children"))).isDisplayed()) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"stepper-children\"]/button[2]"))).click();
		}
		//check that all guests are updated then press search
		if (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"stepper-adults\"]/div/span[1]"))).getText().equals("2")  &&
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"stepper-children\"]/div/span[1]"))).getText().equals("1")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[2]/div/div/div/form/div/div/div[5]/div[4]/button"))).click();
		}
	}
	
	@Test(dependsOnMethods = "search", enabled = true)
	public void checkLocation()
	{
		//check for the searched Location is transfered correctly
		assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[1]/div/button[1]/div"))).getText(), location.split(",")[0], "Applied filter for place is not correct.");
	}
	@Test(dependsOnMethods = "search", enabled = true)
	public void checkNumberofGuests()
	{
		//check for the searched guests is transfered correctly
		assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[1]/div/button[3]/div[1]"))).getText(), "3 guests", "Applied filter for guests is not correct.");
	}
	@Test(dependsOnMethods = "search", enabled = true)
	public void checkDates()
	{
		//check for the searched date is transfered correctly
		assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div/div/div/div[1]/div[1]/div/header/div/div[2]/div[1]/div/button[2]/div"))).getText().contains(startDay+" – "+endDay), "Applied filter for dates is not correct.");
	}
	@Test(dependsOnMethods = "search", enabled = true)
	public void checkNumberOfGuestsForAllResults()
	{
		//check that all the properties displayed on the first page can accommodate number of guests.
		List<WebElement> allElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("_kqh46o"))); 
		for (WebElement element: allElements) {
			if (element.getText().contains("guests")) {
				if(Integer.parseInt(element.getText().substring(0, 2).replaceAll(" ", "")) < 3) {
					assertTrue(false, "Not all the properties displayed on the first page can accommodate number of guests.");					
				}
			}
		}
	}
}