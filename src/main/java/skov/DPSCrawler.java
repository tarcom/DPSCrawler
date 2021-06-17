package skov;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DPSCrawler {

    public static File screenshotFile1;
    public static File screenshotFile2;

    private static DbHandler dbHandler = new DbHandler();

    public static final Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

    public static String mailTxt = "";

    public static void main(String[] args) throws Exception {
        //if (args.length == 0) args = new String[]{"alsk@nykredit.dk"};
        new DPSCrawler().doCrawl(args);
    }

    public void doCrawl(String[] args) throws Exception {

        addToMailText("Welcome!");
        addToMailText("");
        addToMailText("Historisk overblik her: http://10.200.6.15/index.php");
        addToMailText("");

        String pageToScrape = "https://dps.nykreditnet.net/dps/surveillanceoverview.faces";

        WebDriver driver = null;
        try {
            driver = initWebDriver();
            driver.navigate().to(pageToScrape);
            driver.findElement(By.id("j_username")).sendKeys("alsk");
            driver.findElement(By.id("j_password")).sendKeys(new String(Files.readAllBytes(Paths.get("config/pwd"))));
            driver.findElement(By.name("j_idt44")).click();

            //Test that P0 is open...
            //driver.findElement(By.className("rf-trn-hnd-colps")).click(); //click on p0
            //Thread.sleep(3000);
            if (driver.findElements(By.className("rf-trn-lbl")).get(1).getText().indexOf("m0 - ") == -1) { //p0 is unfolded, lets fold again.
                addToMailText("warning: P0 is facing issues!");
                driver.findElement(By.className("rf-trn-hnd-exp")).click(); //click on p0
            }

            //Thread.sleep(100000);

            isGreenAll(driver);

            addToMailText("");
            addToMailText("Link to DPS surveillance overview:");
            addToMailText("https://dps.nykreditnet.net/dps/surveillanceoverview.faces");

            screenshotFile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            pageToScrape = "http://10.200.6.15/index.php";
            driver.navigate().to(pageToScrape);
            screenshotFile2 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            addToMailText("Sending mail to: " + Arrays.asList(args));
            addToMailText("Bye.");
            new MailService().sendMail("Server status from DPS", mailTxt, args);
        } catch (IndexOutOfBoundsException e) {
            System.out.println();
            addToMailText("FATAL: Did you use the right password?");
            System.out.println();
            System.out.println(e);
            addToMailText(e.toString());
            new MailService().sendMail("FAILED: Server status from DPS", mailTxt, new String[]{"alsk@nykredit.dk"});
            throw e;
        } catch (Exception e) {
            System.out.println(e);
            addToMailText(e.toString());
            new MailService().sendMail("FAILED: Server status from DPS", mailTxt, new String[]{"alsk@nykredit.dk"});
            throw e;
        } finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static void isGreenAll(WebDriver driver) throws Exception {

        List<WebElement> webElements = driver.findElements(By.className("rf-tr-nd"));

        for (WebElement webElement : webElements) {

            String info = webElement.getText();
            String env = info.split(" - ")[0];
            String imageFilename = webElement.findElement(By.className("rf-trn-ico-colps")).getAttribute("src");
            //rf-trn-ico-colps rf-trn-ico rf-trn-ico-cst
            int ok = -1;
            if (imageFilename.contains("green-dot.png")) {
                addToMailText(env + " is up and running, info=" + info);
                ok = 1;
            } else {
                addToMailText(env + " ** IS NOT UP!! **  info=" + info);// + ", imageFilename=" + imageFilename);
                ok = 0;
            }
            dbHandler.addDataToDB(env, "na", "na", info, ok, timestamp);

        }
    }

    public static WebDriver initWebDriver() {
        String chromeDriverPath = "seleniumWebDrivers/chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        //System.setProperty("webdriver.chrome.logfile", "chromedriver.log");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--log-level=3", "--silent");
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