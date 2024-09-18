package webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

public class WebDriverManager {
    // initiate private instance
    private static volatile WebDriverManager instance;
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    // create constructor
    private WebDriverManager(){}

    // create private method to initiate driver
    private void initDriver(String browser) {
        switch (browser) {
            case ("safari"):
                tlDriver.set(new SafariDriver());
                break;
            case ("firefox"):
                tlDriver.set(new FirefoxDriver());
                break;
            case ("edge"):
                tlDriver.set(new EdgeDriver());
                break;
            default:
                tlDriver.set(new ChromeDriver());
        }
    }

    // create public to get instance
    public static WebDriverManager getInstance(String browser) {
        if (instance == null) {
            synchronized (WebDriverManager.class) {
                if (instance == null) {
                    instance = new WebDriverManager();
                }
            }
        }
        if (tlDriver.get() == null) {
            instance.initDriver(browser);
        }
        return instance;
    }

    // create public method to get driver
    public WebDriver getDriver() {
         return tlDriver.get();
    }

    // create public method to quit driver
    public static void quitBrowser() {
        if (tlDriver != null) {
            tlDriver.get().quit();
            tlDriver.remove();
        }
    }

}
