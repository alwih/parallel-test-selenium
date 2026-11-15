package io.github.alwih.selenium.driver;

import java.util.List;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

final class DriverFactory {

    private static final Dimension WINDOW_SIZE = new Dimension(1366, 768);

    private DriverFactory() {
    }

    static WebDriver create(String browser) {
        List<String> args = Boolean.getBoolean("headless") ? List.of("--headless") : List.of();
        WebDriver driver = switch (browser) {
            case "chrome" -> new ChromeDriver(new ChromeOptions().addArguments(args));
            case "firefox" -> new FirefoxDriver(new FirefoxOptions().addArguments(args));
            case "edge" -> new EdgeDriver(new EdgeOptions().addArguments(args));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "', use chrome, firefox or edge");
        };
        driver.manage().window().setSize(WINDOW_SIZE);
        return driver;
    }
}
