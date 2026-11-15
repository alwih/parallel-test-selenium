package io.github.alwih.selenium.runner;

import io.github.alwih.selenium.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestRunner {
    private WebDriver driver;

    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        DriverManager.start(browser);
        driver = DriverManager.get();
    }

    @Test
    public void launchGoogle() {
        driver.get("https://google.com");
        System.out.println(Thread.currentThread().getName() + ": " + driver.getTitle());
        Assert.assertEquals(driver.getTitle(), "Google");
    }

    @Test
    public void launchBitbucket() {
        driver.get("https://bitbucket.org");
        System.out.println(Thread.currentThread().getName() + ": " + driver.getTitle());
        Assert.assertEquals(driver.getTitle(), "Bitbucket | Git solution for teams using Jira");
    }

    @Test
    public void launchTokopedia() {
        driver.get("https://www.tokopedia.com");
        System.out.println(Thread.currentThread().getName() + ": " + driver.getTitle());
        Assert.assertEquals(driver.getTitle(), "Situs Jual Beli Online Terlengkap, Mudah & Aman | Tokopedia");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }
}
