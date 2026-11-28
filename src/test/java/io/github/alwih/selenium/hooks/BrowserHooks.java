package io.github.alwih.selenium.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;

public class BrowserHooks {

    private long start;

    @Before
    public void openBrowser() {
        start = System.currentTimeMillis();
        DriverManager.start(browser());
    }

    // Each <test> in testng-cross-browser.xml names its browser; the default suite has no parameter and uses Config
    private String browser() {
        ITestResult result = Reporter.getCurrentTestResult();
        String fromSuite = result == null ? null : result.getTestContext().getCurrentXmlTest().getParameter("browser");
        return fromSuite != null ? fromSuite : Config.get("browser");
    }

    @After
    public void quitBrowser(Scenario scenario) {
        WebDriver driver = DriverManager.get();
        try {
            if (driver != null) {
                RemoteWebDriver remote = (RemoteWebDriver) driver;
                System.out.printf("[%s] %s %s thread=%s session=%s %d ms%n",
                        remote.getCapabilities().getBrowserName(),
                        scenario.isFailed() ? "FAILED" : "PASSED",
                        scenario.getName(),
                        Thread.currentThread().getName(),
                        remote.getSessionId(),
                        System.currentTimeMillis() - start);
                if (scenario.isFailed()) {
                    scenario.attach(remote.getScreenshotAs(OutputType.BYTES), "image/png", "Screenshot");
                    scenario.attach(remote.getCurrentUrl(), "text/plain", "URL");
                }
            }
        } finally {
            DriverManager.quit();
        }
    }
}
