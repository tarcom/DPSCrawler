package skov;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DPSCrawler {

    private static DbHandler dbHandler = new DbHandler();

    public static String mailTxt = "";

    public static void main(String[] args) {
        addToMailText("Welcome!");

        String pageToScrape = "https://dps.nykreditnet.net/dps/surveillanceoverview.faces";

        WebDriver driver = null;
        try {
            driver = initWebDriver();
            driver.navigate().to(pageToScrape);
            driver.findElement(By.id("j_username")).sendKeys("alsk");
            driver.findElement(By.id("j_password")).sendKeys("xxx");
            driver.findElement(By.name("j_idt44")).click();

            //Test that P0 is open...
            //driver.findElement(By.className("rf-trn-hnd-colps")).click(); //click on p0
            //Thread.sleep(3000);
            if (driver.findElements(By.className("rf-trn-lbl")).get(1).getText().indexOf("m0 - ") == -1){ //p0 is unfolded, lets fold again.
                driver.findElement(By.className("rf-trn-hnd-exp")).click(); //click on p0
            }

            //Thread.sleep(100000);

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

            addToMailText("Sending mail to: " + Arrays.asList(args));
            new MailService().sendMail("Server status from DPS", mailTxt, args);
            addToMailText("Bye.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println();
            addToMailText("FATAL: Did you use the right password?");
            System.out.println();
            System.out.println(e);
            addToMailText(e.toString());
            new MailService().sendMail("FAILED: Server status from DPS", mailTxt, new String[]{"alsk@nykredit.dk"});
        } catch (Exception e) {
            System.out.println(e);
            addToMailText(e.toString());
            new MailService().sendMail("FAILED: Server status from DPS", mailTxt, new String[]{"alsk@nykredit.dk"});
        } finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static void isGreen(WebDriver driver, String env, int index) {
        String imageFilename = driver.findElements(By.className("rf-trn-ico-colps")).get(index).getAttribute("src");
        String info = driver.findElements(By.className("rf-trn-lbl")).get(index).getText();
        int ok = -1;
        if (imageFilename.contains("green-dot.png")) {
            addToMailText(env + " is up and running, info=" + info);
            ok = 1;
        } else {
            addToMailText(env + " ** IS NOT UP!! **  info=" + info);// + ", imageFilename=" + imageFilename);
            ok = 0;
        }
        dbHandler.addDataToDB(env, "na", "na", info, ok);
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