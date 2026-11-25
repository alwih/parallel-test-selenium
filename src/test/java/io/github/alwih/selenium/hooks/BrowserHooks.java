package io.github.alwih.selenium.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BrowserHooks {

    private long start;

    @Before
    public void openBrowser() {
        start = System.currentTimeMillis();
        DriverManager.start(Config.get("browser"));
    }

    @After
    public void quitBrowser(Scenario scenario) {
        WebDriver driver = DriverManager.get();
        if (driver != null) {
            RemoteWebDriver remote = (RemoteWebDriver) driver;
            System.out.printf("[%s] %s %s thread=%s session=%s %d ms%n",
                    remote.getCapabilities().getBrowserName(),
                    scenario.isFailed() ? "FAILED" : "PASSED",
                    scenario.getName(),
                    Thread.currentThread().getName(),
                    remote.getSessionId(),
                    System.currentTimeMillis() - start);
        }
        DriverManager.quit();
    }
}
