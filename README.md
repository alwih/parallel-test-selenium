# Parallel Selenium Testing

A UI test project in Java that runs **Cucumber** scenarios in parallel on **TestNG** with Selenium WebDriver, built with **Maven**. The site under test is [SauceDemo](https://www.saucedemo.com), a demo shop made for test automation.

Each scenario runs on its own TestNG thread with its own browser. A Cucumber hook starts the browser through `DriverManager`, which keeps one WebDriver per thread in a `ThreadLocal`, and quits it when the scenario ends, so scenarios never share a browser or depend on each other. Steps talk to the site through page objects that wait for elements explicitly. So far the scenarios cover login: a successful login, rejected logins, logout, and a page that needs a session. They run in Chrome, Firefox or Edge, with or without a visible window, and one extra suite runs all three browsers at the same time.

## Project Structure

```
parallel-test-selenium/
├── .mvn/wrapper/
│   └── maven-wrapper.properties           # Maven version used by ./mvnw (3.10.0)
├── mvnw, mvnw.cmd                         # Maven wrapper scripts (macOS/Linux, Windows)
├── pom.xml                                # Maven build: Selenium 4.52, TestNG 7.12, Cucumber 8.0, Java 17
├── testng.xml                             # Suite: config unit tests, then the Cucumber scenarios (4 at a time)
├── testng-cross-browser.xml               # Suite: the Cucumber scenarios in Chrome, Firefox and Edge at once (2 each)
└── src/test/
    ├── resources/
    │   ├── config.properties              # Default run settings (browser, headless, site, wait timeout, password)
    │   ├── cucumber.properties            # Cucumber defaults (summary output, no publish banner)
    │   └── features/
    │       └── login.feature              # Login, rejected logins, logout, session check
    └── java/io/github/alwih/selenium/
        ├── config/
        │   ├── Config.java                # Reads a setting: -D option, then env var, then config.properties
        │   └── ConfigTest.java            # Unit tests for that order (no browser)
        ├── driver/
        │   ├── DriverFactory.java         # Creates Chrome, Firefox or Edge (headless or not, 1366×768); resolves each browser's driver once
        │   └── DriverManager.java         # One WebDriver per thread (ThreadLocal): start, get, quit
        ├── hooks/
        │   └── BrowserHooks.java          # Opens the browser the suite names before each scenario, logs one line, quits it after
        ├── pages/
        │   ├── BasePage.java              # Explicit waits (visible, clickable) and the current URL path
        │   ├── LoginPage.java             # Login form: open, log in, read the error
        │   └── InventoryPage.java         # Products page: title, open directly, log out from the menu
        ├── runner/
        │   └── CucumberRunner.java        # TestNG entry point: hands scenarios out to parallel threads
        └── steps/
            ├── LoginSteps.java            # Login page steps
            └── InventorySteps.java        # Products page steps
```

## Prerequisites

- **JDK 17 or later** (the build compiles for Java 17)
- **No Maven install needed**: the Maven wrapper (`./mvnw`) downloads Maven 3.10.0 on its first run
- **Google Chrome**, the default browser. Firefox and Microsoft Edge are only needed for `-Dbrowser=firefox`, `-Dbrowser=edge` or the cross-browser suite. Selenium Manager, part of Selenium 4, downloads the matching drivers, and also downloads a browser that isn't installed into `~/.cache/selenium` (Firefox and Edge together take about 1.5 GB)
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

After each scenario the hook prints one line, before it quits the browser:

```
[chrome] PASSED The standard user logs in and sees the products thread=TestNG-PoolService-1 session=0aa74f34a50df9ef43909eb386fccb43 4287 ms
```

It has the browser name reported by the running browser, the result, the scenario name, the thread, the WebDriver session id and the scenario's duration. Every scenario has its own session id, which is how you can check that no two scenarios shared a browser. A thread name proves nothing about that: TestNG reuses thread names across browsers.

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

### All three browsers at once

`-Dsuite` picks the TestNG suite file (`testng.xml` by default). [testng-cross-browser.xml](testng-cross-browser.xml) has one TestNG `<test>` per browser (Chrome, Firefox, Edge), each with a `browser` parameter and only `CucumberRunner`, and runs the three at the same time. Every browser runs all the scenarios, so 9 scenarios become 27:

```bash
./mvnw test -Dsuite=testng-cross-browser.xml -Dheadless=true
```

- **Browsers open at once = browsers × scenarios per browser.** `data-provider-thread-count` counts per browser, not for the whole suite, so the default of 2 means 3 × 2 = 6 browsers at once. Change it with `-Ddataproviderthreadcount=N`; with `N=1` it is 3 at once. `thread-count="3"` in the suite must stay at or above the number of browsers, or some browsers wait for the others instead of running together.
- **The suite beats `-Dbrowser` here.** A `<test>` named Firefox runs Firefox whatever `-Dbrowser` or `BROWSER` says. The default `testng.xml` has no browser parameter, so it still uses the `browser` setting. A wrong value in the XML fails that browser's scenarios with `Unsupported browser`, and the other browsers still run.
- **Finding a failing browser.** Cucumber prints one summary block per browser, with no browser name, and a failure shows only `feature:line`. Search the output for `FAILED` lines instead: each one names the browser.
- **Use `-Dheadless=true`** unless you want to watch six windows open.
- `ConfigTest` is only in `testng.xml`, so this suite runs scenarios only.

Times on one laptop, headless, with browsers and drivers already downloaded. Each is the median of 3 runs (range in brackets) of Maven's `Time elapsed`, which includes the unit tests:

| Run | Scenarios | Time |
|---|---|---|
| Chrome, 1 scenario at a time (`-Ddataproviderthreadcount=1`) | 9 | 12.1 s (11.8–14.3 s) |
| Chrome, 4 at a time (`./mvnw test -Dheadless=true`) | 9 | 7.0 s (6.6–8.2 s) |
| Chrome, Firefox and Edge, 2 at a time each (6 browsers) | 27 | 35.8 s (32.6–37.2 s) |

Four at a time is about 1.7 times faster than one at a time for Chrome. The cross-browser run does three times the scenarios in about five times the time of the 4-thread Chrome run, so it isn't a speedup; its time is set by Firefox and Edge, which start more slowly than Chrome (one run each at 4 threads, 9 scenarios: Firefox 16.1 s, Edge 12.5 s). Times change from run to run and depend on the machine and the network.

Every browser opens at 1366×768, headless or not, so pages lay out the same way in both modes. The after-scenario hook always runs and quits only a browser that actually started, so a browser that fails to start shows its real error.

## What Gets Tested

**Run settings** (`ConfigTest`, 6 unit tests, no browser): a `-D` option beats an environment variable, which beats `config.properties`; `base.url` is looked up as the env var `BASE_URL`; a missing setting fails with its name; and `config.properties` provides `headless`.

**Login** ([login.feature](src/test/resources/features/login.feature), tagged `@login`, 9 scenarios). Every scenario starts on the login page and checks the URL path before what's on the page:

- `@smoke @positive`: `standard_user` logs in and lands on the products page (`/inventory.html`, title "Products").
- `@negative`, one Scenario Outline with 5 rows: a locked-out user, a wrong password, an unknown user, an empty username and an empty password. Each stays on the login page with the form visible and shows its own error, e.g. "Epic sadface: Sorry, this user has been locked out.".
- `@edge`: a username with spaces around it is rejected; the site doesn't trim it.
- `@positive`: logging out from the menu returns to the login page, and opening the products page afterwards sends you back to login with "You can only access '/inventory.html' when you are logged in.".
- `@edge`: opening the products page without logging in shows the same redirect and error.

## Modifying Test Data

- **Scenarios** are Gherkin `.feature` files in [src/test/resources/features](src/test/resources/features). Their steps are Java methods in the [steps](src/test/java/io/github/alwih/selenium/steps) package. A new `.feature` file in that folder runs without any other change. Tag each feature with its area (`@login`) and each scenario with its intent (`@positive`, `@negative` or `@edge`).
- **Rejected logins** are rows in the `Examples` table of [login.feature](src/test/resources/features/login.feature): add a row with the username, password, expected error and a short note. Cucumber trims spaces around table cells, which is why the username-with-spaces case is its own scenario.
- **Locators** live in the page objects in the [pages](src/test/java/io/github/alwih/selenium/pages) package, as `data-test` attributes where the site has them. Steps never find elements themselves.
- **Run settings** live in [config.properties](src/test/resources/config.properties). Each one can be overridden without editing the file, and the first match wins:
  1. a `-D` option on the Maven command, e.g. `-Dbrowser=firefox`
  2. an environment variable named after the key in upper case, with dots as underscores (`browser` → `BROWSER`, `base.url` → `BASE_URL`)
  3. the value in `config.properties`
- **Browser**: the `browser` setting, `chrome` by default; `firefox` and `edge` also work. Any other value fails every scenario with `Unsupported browser`. Safari isn't supported: it runs one session at a time and has no headless mode.
- **Site under test**: the `base.url` setting, `https://www.saucedemo.com`.
- **Headless mode**: the `headless` setting, `false` by default (browsers open in a window). Any value other than `true` means a visible window, so a typo runs headed.
- **Wait timeout**: the `timeout.seconds` setting, `10` by default. Page objects wait up to this long for an element before failing.
- **Password**: the `sauce.password` setting, used by the "I log in as" step. It is kept in `config.properties` because it isn't a secret: SauceDemo prints it on its login page for every demo user.
- **Scenarios at once**: `data-provider-thread-count` in [testng.xml](testng.xml). `-Ddataproviderthreadcount=N` overrides it for one run. In [testng-cross-browser.xml](testng-cross-browser.xml) it is per browser.
- **Which suite runs**: `-Dsuite=testng-cross-browser.xml` (default `testng.xml`), set by the `suite` property in [pom.xml](pom.xml).
- **Window size** is set in [DriverFactory.java](src/test/java/io/github/alwih/selenium/driver/DriverFactory.java).
- **Cucumber output**: the `cucumber.plugin` line in [cucumber.properties](src/test/resources/cucumber.properties).

## Troubleshooting

**`error: release version 17 not supported`?**
- Maven is running on a JDK older than 17. Check with `./mvnw -v`, then install JDK 17 or later and point `JAVA_HOME` at it.

**`NoSuchDriverException: Unable to obtain: geckodriver` (or `msedgedriver`)?**
- Selenium Manager couldn't download Firefox or Edge, or its driver. Check the internet connection, or install the browser yourself.

**The first run of a browser takes minutes, or some scenarios fail while downloading?**
- Selenium Manager downloads a browser or driver that is missing or newer than the cached one, and the first scenarios of that browser wait for it. A slow connection can time out the download and fail a few scenarios. Run once with `-Dbrowser=firefox` (and `edge`) to warm the cache before a cross-browser run, then run again.

**A scenario failed but has no `[browser] FAILED` line?**
- The log line needs a running browser. When a browser fails to start, the scenario fails with the start error (for example `NoSuchDriverException`) and prints no line.

**Warning `Unable to find an exact match for CDP version ...`?**
- The installed Chrome is newer than the Selenium release. These tests don't use Chrome DevTools, so it's harmless.

**`IllegalStateException: Missing setting 'x'`?**
- The code asked for a setting that isn't in `config.properties` and wasn't passed as `-Dx=...` or an environment variable. Add it to the file or pass it on the command line.

**`IllegalArgumentException: Unsupported browser 'x'`?**
- The `browser` setting (from `-Dbrowser`, `BROWSER` or `config.properties`) isn't `chrome`, `firefox` or `edge`. Fix the value.

**`TimeoutException` waiting for `[data-test='username']`?**
- The login form didn't appear within `timeout.seconds` (10 by default). Check the internet connection, and check that `base.url` (or `BASE_URL`) points at SauceDemo. On a slow connection, raise it for one run with `-Dtimeout.seconds=20`.

**A deleted feature file or step class still runs?**
- Its compiled copy is still in `target/test-classes`. Run `./mvnw clean test`.

## Next Steps

- Test the rest of the SauceDemo shop: product list and sorting, cart and checkout
- Allure reports with a screenshot of each failure
- Run the browsers on a Selenium Grid in Docker
- Run the tests in GitHub Actions on every pull request and every night

## Learning Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/)
- [Selenium browser options](https://www.selenium.dev/documentation/webdriver/browsers/) (arguments such as `--headless`)
- [Selenium waits](https://www.selenium.dev/documentation/webdriver/waits/) (explicit waits)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Gherkin reference](https://cucumber.io/docs/gherkin/reference/) (Background, Scenario Outline and Examples)
- [Page object models](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [Cucumber with TestNG](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-testng) (running scenarios in parallel)
- [TestNG Documentation](https://testng.org/) (see the sections on parallel running and data providers)
- [Maven Surefire with TestNG](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/)
- [Setting `--release` in the Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- [Java `ThreadLocal`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ThreadLocal.html)
- [Java `Properties`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Properties.html) (the `config.properties` file format)
