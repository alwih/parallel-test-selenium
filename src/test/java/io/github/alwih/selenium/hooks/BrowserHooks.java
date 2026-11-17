package io.github.alwih.selenium.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;

public class BrowserHooks {

    @Before
    public void openBrowser() {
        DriverManager.start(Config.get("browser"));
    }

    @After
    public void quitBrowser() {
        DriverManager.quit();
    }
}
