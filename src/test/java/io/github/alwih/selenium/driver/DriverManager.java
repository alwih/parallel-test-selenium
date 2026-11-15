package io.github.alwih.selenium.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void start(String browser) {
        DRIVER.set(DriverFactory.create(browser));
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            DRIVER.remove();
            driver.quit();
        }
    }
}
