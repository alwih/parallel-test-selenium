package io.github.alwih.selenium.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

final class DriverFactory {

    private DriverFactory() {
    }

    static WebDriver create(String browser) {
        return switch (browser) {
            case "chrome" -> new ChromeDriver();
            case "firefox" -> new FirefoxDriver();
            case "edge" -> new EdgeDriver();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "', use chrome, firefox or edge");
        };
    }
}
