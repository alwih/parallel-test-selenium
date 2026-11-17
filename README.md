# Parallel Selenium Testing

A UI test project in Java that runs **Cucumber** scenarios in parallel on **TestNG** with Selenium WebDriver, built with **Maven**. The site under test is [SauceDemo](https://www.saucedemo.com), a demo shop made for test automation.

Each scenario runs on its own TestNG thread with its own browser. A Cucumber hook starts the browser through `DriverManager`, which keeps one WebDriver per thread in a `ThreadLocal`, and quits it when the scenario ends, so scenarios never share a browser or depend on each other. So far there is one smoke scenario, which opens the SauceDemo login page. It runs in Chrome, Firefox or Edge, with or without a visible window.

## Project Structure

```
parallel-test-selenium/
├── .mvn/wrapper/
│   └── maven-wrapper.properties           # Maven version used by ./mvnw (3.10.0)
├── mvnw, mvnw.cmd                         # Maven wrapper scripts (macOS/Linux, Windows)
├── pom.xml                                # Maven build: Selenium 4.52, TestNG 7.12, Cucumber 8.0, Java 17
├── testng.xml                             # Suite: config unit tests, then the Cucumber scenarios (4 at a time)
└── src/test/
    ├── resources/
    │   ├── config.properties              # Default run settings (headless, browser, base.url)
    │   ├── cucumber.properties            # Cucumber defaults (summary output, no publish banner)
    │   └── features/
    │       └── login.feature              # Login page scenario
    └── java/io/github/alwih/selenium/
        ├── config/
        │   ├── Config.java                # Reads a setting: -D option, then env var, then config.properties
        │   └── ConfigTest.java            # Unit tests for that order (no browser)
        ├── driver/
        │   ├── DriverFactory.java         # Creates Chrome, Firefox or Edge (headless or not, 1366×768)
        │   └── DriverManager.java         # One WebDriver per thread (ThreadLocal): start, get, quit
        ├── hooks/
        │   └── BrowserHooks.java          # Opens a browser before each scenario, quits it after
        ├── runner/
        │   └── CucumberRunner.java        # TestNG entry point: hands scenarios out to parallel threads
        └── steps/
            └── LoginSteps.java            # Step definitions for login.feature
```

## Prerequisites

- **JDK 17 or later** (the build compiles for Java 17)
- **No Maven install needed**: the Maven wrapper (`./mvnw`) downloads Maven 3.10.0 on its first run
- **Google Chrome**, the default browser. Firefox and Microsoft Edge are only needed for `-Dbrowser=firefox` or `-Dbrowser=edge`. Selenium Manager, part of Selenium 4, downloads the matching drivers, and also downloads a browser that isn't installed into `~/.cache/selenium` (Firefox and Edge together take about 1.5 GB)
- **Git** and an **internet connection** (the scenarios open saucedemo.com)

## Setup Instructions

### 1. Install a JDK

On macOS with Homebrew:

```bash
brew install --cask temurin@17
```

Check that Java 17 or later is the active version:

```bash
java -version
```

### 2. Clone the Repository

```bash
git clone https://github.com/alwih/parallel-test-selenium.git
cd parallel-test-selenium
```

### 3. Install Dependencies

The wrapper downloads Maven, then Maven downloads Selenium, TestNG and Cucumber:

```bash
./mvnw test-compile
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Running the Tests

```bash
./mvnw test
```

Maven Surefire runs the suite in [testng.xml](testng.xml). It runs the settings unit tests first (they need no browser), then `CucumberRunner`, which runs every scenario in [src/test/resources/features](src/test/resources/features). The runner hands scenarios to TestNG through a parallel DataProvider, so they run on separate threads, up to 4 at once (`data-provider-thread-count` in `testng.xml`). Each scenario opens its own browser before it starts and quits it when it ends, even when the scenario fails. At the end, Cucumber prints a summary: scenario and step counts (the before and after hooks count as steps), and the feature line and error of each failed scenario. A run takes a few seconds once the browser and driver are downloaded.

To run only the scenarios with a given tag, pass a [tag expression](https://cucumber.io/docs/cucumber/api/#tag-expressions):

```bash
./mvnw test -Dcucumber.filter.tags="@smoke"
```

To change how many scenarios run at once:

```bash
./mvnw test -Ddataproviderthreadcount=2
```

To choose the browser, or to run without opening browser windows, use a `-D` option or the matching environment variable:

```bash
./mvnw test -Dbrowser=firefox
```

```bash
./mvnw test -Dheadless=true
```

```bash
BROWSER=edge HEADLESS=true ./mvnw test
```

To run only the settings unit tests, or only the scenarios:

```bash
./mvnw test -Dtest=ConfigTest
```

```bash
./mvnw test -Dtest=CucumberRunner
```

`-Dtest` bypasses `testng.xml`, so its limit of 4 scenarios at once doesn't apply. Add `-Ddataproviderthreadcount=4` to keep it.

Every browser opens at 1366×768, headless or not, so pages lay out the same way in both modes. The after-scenario hook always runs and quits only a browser that actually started, so a browser that fails to start shows its real error.

## What Gets Tested

**Run settings** (`ConfigTest`, 6 unit tests, no browser): a `-D` option beats an environment variable, which beats `config.properties`; `base.url` is looked up as the env var `BASE_URL`; a missing setting fails with its name; and `config.properties` provides `headless`.

**Login page** ([login.feature](src/test/resources/features/login.feature), tagged `@login`; the scenario is `@smoke @positive`): opening SauceDemo lands on the login page (path `/`) and shows the username field, the password field and the login button.

## Modifying Test Data

- **Scenarios** are Gherkin `.feature` files in [src/test/resources/features](src/test/resources/features). Their steps are Java methods in the [steps](src/test/java/io/github/alwih/selenium/steps) package. A new `.feature` file in that folder runs without any other change. Tag each feature with its area (`@login`) and each scenario with its intent (`@positive`, `@negative` or `@edge`).
- **Run settings** live in [config.properties](src/test/resources/config.properties). Each one can be overridden without editing the file, and the first match wins:
  1. a `-D` option on the Maven command, e.g. `-Dbrowser=firefox`
  2. an environment variable named after the key in upper case, with dots as underscores (`browser` → `BROWSER`, `base.url` → `BASE_URL`)
  3. the value in `config.properties`
- **Browser**: the `browser` setting, `chrome` by default; `firefox` and `edge` also work. Any other value fails every scenario with `Unsupported browser`. Safari isn't supported: it runs one session at a time and has no headless mode.
- **Site under test**: the `base.url` setting, `https://www.saucedemo.com`.
- **Headless mode**: the `headless` setting, `false` by default (browsers open in a window). Any value other than `true` means a visible window, so a typo runs headed.
- **Scenarios at once**: `data-provider-thread-count` in [testng.xml](testng.xml). `-Ddataproviderthreadcount=N` overrides it for one run.
- **Window size** is set in [DriverFactory.java](src/test/java/io/github/alwih/selenium/driver/DriverFactory.java).
- **Cucumber output**: the `cucumber.plugin` line in [cucumber.properties](src/test/resources/cucumber.properties).

## Troubleshooting

**`error: release version 17 not supported`?**
- Maven is running on a JDK older than 17. Check with `./mvnw -v`, then install JDK 17 or later and point `JAVA_HOME` at it.

**`NoSuchDriverException: Unable to obtain: geckodriver` (or `msedgedriver`)?**
- Selenium Manager couldn't download Firefox or Edge, or its driver. Check the internet connection, or install the browser yourself.

**Warning `Unable to find an exact match for CDP version ...`?**
- The installed Chrome is newer than the Selenium release. These tests don't use Chrome DevTools, so it's harmless.

**`IllegalStateException: Missing setting 'x'`?**
- The code asked for a setting that isn't in `config.properties` and wasn't passed as `-Dx=...` or an environment variable. Add it to the file or pass it on the command line.

**`IllegalArgumentException: Unsupported browser 'x'`?**
- The `browser` setting (from `-Dbrowser`, `BROWSER` or `config.properties`) isn't `chrome`, `firefox` or `edge`. Fix the value.

**`TimeoutException` waiting for `[data-test='username']`?**
- The login form didn't appear within 10 seconds. Check the internet connection, and check that `base.url` (or `BASE_URL`) points at SauceDemo.

**A deleted feature file or step class still runs?**
- Its compiled copy is still in `target/test-classes`. Run `./mvnw clean test`.

## Next Steps

- Test the SauceDemo shop: login, product list and sorting, cart and checkout
- Run every scenario on Chrome, Firefox and Edge at the same time
- Allure reports with a screenshot of each failure
- Run the browsers on a Selenium Grid in Docker
- Run the tests in GitHub Actions on every pull request and every night

## Learning Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/)
- [Selenium browser options](https://www.selenium.dev/documentation/webdriver/browsers/) (arguments such as `--headless`)
- [Selenium waits](https://www.selenium.dev/documentation/webdriver/waits/) (explicit waits)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Gherkin reference](https://cucumber.io/docs/gherkin/reference/)
- [Cucumber with TestNG](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-testng) (running scenarios in parallel)
- [TestNG Documentation](https://testng.org/) (see the sections on parallel running and data providers)
- [Maven Surefire with TestNG](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/)
- [Setting `--release` in the Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- [Java `ThreadLocal`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ThreadLocal.html)
- [Java `Properties`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Properties.html) (the `config.properties` file format)
