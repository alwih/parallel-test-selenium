# Parallel Selenium Testing

A Selenium WebDriver project in Java that runs several browsers at the same time with **TestNG**, built with **Maven**.

Each test thread gets its own browser from `DriverManager`, which keeps one WebDriver per thread in a `ThreadLocal`, so Chrome, Firefox and Edge sessions run side by side without sharing state. The suite opens three public websites in each browser and checks their page titles: nine browser sessions in parallel, with or without a visible window.

## Project Structure

```
parallel-test-selenium/
├── .mvn/wrapper/
│   └── maven-wrapper.properties           # Maven version used by ./mvnw (3.10.0)
├── mvnw, mvnw.cmd                         # Maven wrapper scripts (macOS/Linux, Windows)
├── pom.xml                                # Maven build: Selenium 4.52, TestNG 7.12, Java 17
├── testng.xml                             # Suite: config unit tests + 9 browser tests (3 browsers × 3 sites) in parallel
└── src/test/
    ├── resources/
    │   └── config.properties              # Default run settings (headless)
    └── java/io/github/alwih/selenium/
        ├── config/
        │   ├── Config.java                # Reads a setting: -D option, then env var, then config.properties
        │   └── ConfigTest.java            # Unit tests for that order (no browser)
        ├── driver/
        │   ├── DriverFactory.java         # Creates Chrome, Firefox or Edge (headless or not, 1366×768)
        │   └── DriverManager.java         # One WebDriver per thread (ThreadLocal): start, get, quit
        └── runner/
            └── TestRunner.java            # Opens a site and checks its page title
```

## Prerequisites

- **JDK 17 or later** (the build compiles for Java 17)
- **No Maven install needed**: the Maven wrapper (`./mvnw`) downloads Maven 3.10.0 on its first run
- **Google Chrome, Firefox and Microsoft Edge**. Selenium Manager, part of Selenium 4, downloads the matching drivers, and also downloads a browser that isn't installed into `~/.cache/selenium` (Firefox and Edge together take about 1.5 GB)
- **Git** and an **internet connection** (the tests open live websites)

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

The wrapper downloads Maven, then Maven downloads Selenium and TestNG:

```bash
./mvnw test-compile
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Running the Tests

```bash
./mvnw test
```

Maven Surefire runs the suite in [testng.xml](testng.xml). The first block runs the settings unit tests, which need no browser and finish in milliseconds. The suite uses `parallel="tests"` with `thread-count="9"`, so every browser `<test>` block runs in its own thread with its own browser, all at the same time. Each thread prints its name and the page title, which shows the sessions running side by side. A run takes under half a minute once the browsers and drivers are downloaded.

To run without opening browser windows, use either the `-D` option or the environment variable:

```bash
./mvnw test -Dheadless=true
```

```bash
HEADLESS=true ./mvnw test
```

To run only the settings unit tests:

```bash
./mvnw test -Dtest=ConfigTest
```

Every browser opens at 1366×768, headless or not, so pages lay out the same way in both modes. Teardown always runs and quits only a browser that actually started, so a browser that fails to start shows its real error.

## What Gets Tested

**Run settings** (`ConfigTest`, 6 unit tests, no browser): a `-D` option beats an environment variable, which beats `config.properties`; `base.url` is looked up as the env var `BASE_URL`; a missing setting fails with its name; and `config.properties` provides `headless`.

**Page titles**: each site is opened in Chrome, Firefox and Edge (9 tests), and the page title must match exactly:

| Test method | Site | Expected title |
|---|---|---|
| `launchGoogle` | https://google.com | `Google` |
| `launchBitbucket` | https://bitbucket.org | `Bitbucket \| Git solution for teams using Jira` |
| `launchTokopedia` | https://www.tokopedia.com | `Situs Jual Beli Online Terlengkap, Mudah & Aman \| Tokopedia` |

The Bitbucket check currently fails in all three browsers: the site's title is now `Bitbucket Home Page`.

## Modifying Test Data

- **Browser and site combinations** live in [testng.xml](testng.xml). Each `<test>` block sets a `browser` parameter and includes one test method. Add or remove blocks to change the combinations, and keep `thread-count` equal to the number of browser blocks to run them all at once (the unit-test block frees its thread within milliseconds).
- **Browser values**: `chrome`, `firefox` and `edge`. Any other value stops that test with `Unsupported browser`. Safari isn't supported: it runs one session at a time and has no headless mode.
- **Run settings** live in [config.properties](src/test/resources/config.properties). Each one can be overridden without editing the file, and the first match wins:
  1. a `-D` option on the Maven command, e.g. `-Dheadless=true`
  2. an environment variable named after the key in upper case, with dots as underscores (`headless` → `HEADLESS`, `base.url` → `BASE_URL`)
  3. the value in `config.properties`
- **Headless mode**: the `headless` setting, `false` by default (browsers open in a window). Any value other than `true` means a visible window, so a typo runs headed.
- **Window size** is set in [DriverFactory.java](src/test/java/io/github/alwih/selenium/driver/DriverFactory.java).
- **URLs and expected titles** live in [TestRunner.java](src/test/java/io/github/alwih/selenium/runner/TestRunner.java).

## Troubleshooting

**`error: release version 17 not supported`?**
- Maven is running on a JDK older than 17. Check with `./mvnw -v`, then install JDK 17 or later and point `JAVA_HOME` at it.

**`NoSuchDriverException: Unable to obtain: geckodriver` (or `msedgedriver`)?**
- Selenium Manager couldn't download the browser or its driver. Check the internet connection, or install Firefox or Edge yourself.

**Warning `Unable to find an exact match for CDP version ...`?**
- The installed Chrome is newer than the Selenium release. These tests don't use Chrome DevTools, so it's harmless.

**`IllegalStateException: Missing setting 'x'`?**
- The code asked for a setting that isn't in `config.properties` and wasn't passed as `-Dx=...` or an environment variable. Add it to the file or pass it on the command line.

**`IllegalArgumentException: Unsupported browser 'x'`?**
- A `browser` parameter in `testng.xml` isn't `chrome`, `firefox` or `edge`. Fix the value.

**A title check fails?**
- These are live third-party sites, and their titles change. Open the site, copy the current title and update the expected value in `TestRunner.java`.

**Different title results with `-Dheadless=true`?**
- Some sites serve headless Chrome and Edge a different page. Tokopedia shows a bot check titled `www.tokopedia.com`, so that check fails headless, and Bitbucket still shows its old title, so that check passes. Firefox gets the same pages headless and headed.

## Next Steps

- Move to Cucumber scenarios on TestNG, running in parallel
- Test the SauceDemo shop: login, product list and sorting, cart and checkout
- Run every scenario on Chrome, Firefox and Edge at the same time
- Allure reports with a screenshot of each failure
- Run the browsers on a Selenium Grid in Docker
- Run the tests in GitHub Actions on every pull request and every night

## Learning Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/)
- [Selenium browser options](https://www.selenium.dev/documentation/webdriver/browsers/) (arguments such as `--headless`)
- [TestNG Documentation](https://testng.org/) (see the section on parallel running)
- [Maven Surefire with TestNG](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/)
- [Setting `--release` in the Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- [Java `ThreadLocal`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ThreadLocal.html)
- [Java `Properties`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Properties.html) (the `config.properties` file format)
