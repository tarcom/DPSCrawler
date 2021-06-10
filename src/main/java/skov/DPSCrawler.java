package skov;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DPSCrawler {

    public static void main(String[] args) {
        System.out.println("Welcome!");

        String pageToScrape = "https://dps.nykreditnet.net/dps/surveillanceoverview.faces";

        WebDriver driver = null;
        try {
            driver = initWebDriver();
            driver.navigate().to(pageToScrape);
            driver.findElement(By.id("j_username")).sendKeys("alsk");
            driver.findElement(By.id("j_password")).sendKeys("xxx"); //DO NOT COMMIT TO GIT
            driver.findElement(By.name("j_idt44")).click();

            isGreen(driver, "p0 ", 0);
            isGreen(driver, "m0 ", 1);
            isGreen(driver, "es1", 24);
            isGreen(driver, "et1", 23);
            isGreen(driver, "et4", 20);
            isGreen(driver, "t9 ", 9);
            isGreen(driver, "t6 ", 12);
            isGreen(driver, "t4 ", 14);

            System.out.println("Bye.");

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static void isGreen(WebDriver driver, String logicalName, int index) {
        String imageFilename = driver.findElements(By.className("rf-trn-ico-colps")).get(index).getAttribute("src");
        String info = driver.findElements(By.className("rf-trn-lbl")).get(index).getText();
        if (imageFilename.contains("green-dot.png")) {
            System.out.println(logicalName + " is up and running, info=" + info);
        } else {
            System.out.println(logicalName + " is NOT UP!!        info=" + info + ", imageFilename=" + imageFilename);
        }
    }

    public static WebDriver initWebDriver() {
        String chromeDriverPath = "seleniumWebDrivers/chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        //System.setProperty("webdriver.chrome.logfile", "chromedriver.log");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--log-level=3", "--silent");
        WebDriver driver = new ChromeDriver(options);
        //driver.manage().timeouts().implicitlyWait(TIME_OUT_WEBPAGE_SELENIUM_GOOGLE_CHROME_SEC, TimeUnit.SECONDS);
        //((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        return driver;
    }
}