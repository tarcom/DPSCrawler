package skov;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DPSCrawler {

    public static String mailTxt = "";

    public static void main(String[] args) {
        addToMailText("Welcome!");

        String pageToScrape = "https://dps.nykreditnet.net/dps/surveillanceoverview.faces";

        WebDriver driver = null;
        try {
            driver = initWebDriver();
            driver.navigate().to(pageToScrape);
            driver.findElement(By.id("j_username")).sendKeys("alsk");
            driver.findElement(By.id("j_password")).sendKeys("xxx"); //!!!
            driver.findElement(By.name("j_idt44")).click();

            isGreen(driver, "p0 ", 0);
            isGreen(driver, "m0 ", 1);
            isGreen(driver, "es1", 24);
            isGreen(driver, "et1", 23);
            isGreen(driver, "et4", 20);
            isGreen(driver, "t9 ", 9);
            isGreen(driver, "t6 ", 12);
            isGreen(driver, "t4 ", 14);

            addToMailText("");
            addToMailText("Link to DPS surveillance overview:");
            addToMailText("https://dps.nykreditnet.net/dps/surveillanceoverview.faces");

            addToMailText("Bye.");
            new MailService().sendMail("Server status from DPS", mailTxt);

        } catch (IndexOutOfBoundsException e) {
            addToMailText("FATAL: Did you use the right password?");
            System.out.println(e);
            addToMailText(e.toString());
        } catch (Exception e) {
            System.out.println(e);
            addToMailText(e.toString());
        } finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static void isGreen(WebDriver driver, String logicalName, int index) {
        String imageFilename = driver.findElements(By.className("rf-trn-ico-colps")).get(index).getAttribute("src");
        String info = driver.findElements(By.className("rf-trn-lbl")).get(index).getText();
        if (imageFilename.contains("green-dot.png")) {
            addToMailText(logicalName + " is up and running, info=" + info);
        } else {
            addToMailText(logicalName + " ** IS NOT UP!! **  info=" + info);// + ", imageFilename=" + imageFilename);
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

    public static String addToMailText(String txt) {

        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        txt = ts + " " + txt;
        System.out.println(txt);
        mailTxt += "\n" + txt;
        return mailTxt;
    }

}