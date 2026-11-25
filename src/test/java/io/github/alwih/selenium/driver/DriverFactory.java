package io.github.alwih.selenium.driver;

import io.github.alwih.selenium.config.Config;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverFinder;

final class DriverFactory {

    private static final Dimension WINDOW_SIZE = new Dimension(1366, 768);

    // A DriverFinder keeps the first Selenium Manager answer. Concurrent Selenium Manager calls slow every
    // start down, so each browser asks once and every later start reuses the answer.
    private static final Map<String, DriverFinder> FINDERS = Map.of(
            "chrome", new DriverFinder(ChromeDriverService.createDefaultService(), new ChromeOptions()),
            "firefox", new DriverFinder(GeckoDriverService.createDefaultService(), new FirefoxOptions()),
            "edge", new DriverFinder(EdgeDriverService.createDefaultService(), new EdgeOptions()));

    private DriverFactory() {
    }

    static WebDriver create(String browser) {
        DriverFinder finder = FINDERS.get(browser);
        if (finder == null) {
            throw new IllegalArgumentException("Unsupported browser '" + browser + "', use chrome, firefox or edge");
        }
        File driverPath;
        String browserPath;
        synchronized (finder) {
            driverPath = new File(finder.getDriverPath());
            browserPath = finder.hasBrowserPath() ? finder.getBrowserPath() : null;
        }
        List<String> args = Boolean.parseBoolean(Config.get("headless")) ? List.of("--headless") : List.of();
        WebDriver driver = switch (browser) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions().addArguments(args);
                if (browserPath != null) {
                    options.setBinary(browserPath);
                }
                yield new ChromeDriver(new ChromeDriverService.Builder().usingDriverExecutable(driverPath).build(), options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions().addArguments(args);
                if (browserPath != null) {
                    options.setBinary(browserPath);
                }
                yield new FirefoxDriver(new GeckoDriverService.Builder().usingDriverExecutable(driverPath).build(), options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions().addArguments(args);
                if (browserPath != null) {
                    options.setBinary(browserPath);
                }
                yield new EdgeDriver(new EdgeDriverService.Builder().usingDriverExecutable(driverPath).build(), options);
            }
            default -> throw new IllegalStateException("No driver for " + browser);
        };
        driver.manage().window().setSize(WINDOW_SIZE);
        return driver;
    }
}
