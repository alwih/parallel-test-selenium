package io.github.alwih.selenium.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

// cucumber-testng is deprecated for removal in Cucumber 8; we stay on TestNG while surefire 3.5.x runs testng.xml
@SuppressWarnings("removal")
@CucumberOptions(features = "classpath:features",
        glue = {"io.github.alwih.selenium.steps", "io.github.alwih.selenium.hooks"})
public class CucumberRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios(ITestContext context) {
        return super.scenarios(context);
    }
}
