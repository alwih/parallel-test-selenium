# Parallel Selenium Testing

A Selenium WebDriver project in Java that runs several browsers at the same time with **TestNG**, built with **Maven**.

Each test thread gets its own WebDriver through a `ThreadLocal`, so Chrome, Firefox and Edge sessions run side by side without sharing state. The suite opens three public websites in each browser and checks their page titles: nine browser sessions in parallel.

## Project Structure

```
parallel-test-selenium/
├── .mvn/wrapper/
│   └── maven-wrapper.properties           # Maven version used by ./mvnw (3.10.0)
├── mvnw, mvnw.cmd                         # Maven wrapper scripts (macOS/Linux, Windows)
├── pom.xml                                # Maven build: Selenium 4.52, TestNG 7.12, Java 17
├── testng.xml                             # Suite: 9 tests (3 browsers × 3 sites) run in parallel
└── src/test/java/
    ├── runner/
    │   └── TestRunner.java                # Opens a site and checks its page title
    └── webdriver/
        └── WebDriverManager.java          # One WebDriver per thread (ThreadLocal) behind a singleton
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

Maven Surefire runs the suite in [testng.xml](testng.xml). It uses `parallel="tests"` with `thread-count="9"`, so every `<test>` block runs in its own thread with its own browser, all at the same time. Each thread prints its name and the page title, which shows the sessions running side by side. A run takes under half a minute once the browsers and drivers are downloaded.

## What Gets Tested

Each site is opened in Chrome, Firefox and Edge (9 tests), and the page title must match exactly:

| Test method | Site | Expected title |
|---|---|---|
| `launchGoogle` | https://google.com | `Google` |
| `launchBitbucket` | https://bitbucket.org | `Bitbucket \| Git solution for teams using Jira` |
| `launchTokopedia` | https://www.tokopedia.com | `Situs Jual Beli Online Terlengkap, Mudah & Aman \| Tokopedia` |

The Bitbucket check currently fails in all three browsers: the site's title is now `Bitbucket Home Page`.

## Modifying Test Data

- **Browser and site combinations** live in [testng.xml](testng.xml). Each `<test>` block sets a `browser` parameter and includes one test method. Add or remove blocks to change the combinations, and keep `thread-count` equal to the number of blocks to run them all at once.
- **Browser values**: `chrome`, `firefox`, `edge` and `safari`. Any other value falls back to Chrome. Safari runs only one session at a time, so it isn't in the parallel suite.
- **URLs and expected titles** live in [TestRunner.java](src/test/java/runner/TestRunner.java).

## Troubleshooting

**`error: release version 17 not supported`?**
- Maven is running on a JDK older than 17. Check with `./mvnw -v`, then install JDK 17 or later and point `JAVA_HOME` at it.

**`NoSuchDriverException: Unable to obtain: geckodriver` (or `msedgedriver`)?**
- Selenium Manager couldn't download the browser or its driver. Check the internet connection, or install Firefox or Edge yourself.

**Warning `Unable to find an exact match for CDP version ...`?**
- The installed Chrome is newer than the Selenium release. These tests don't use Chrome DevTools, so it's harmless.

**A title check fails?**
- These are live third-party sites, and their titles change. Open the site, copy the current title and update the expected value in `TestRunner.java`.

## Next Steps

- Make the per-thread driver setup safer and add a headless mode
- Read run settings from a properties file, environment variables and `-D` options
- Move to Cucumber scenarios on TestNG, running in parallel
- Test the SauceDemo shop: login, product list and sorting, cart and checkout
- Run every scenario on Chrome, Firefox and Edge at the same time
- Allure reports with a screenshot of each failure
- Run the browsers on a Selenium Grid in Docker
- Run the tests in GitHub Actions on every pull request and every night

## Learning Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/)
- [TestNG Documentation](https://testng.org/) (see the section on parallel running)
- [Maven Surefire with TestNG](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/)
- [Setting `--release` in the Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- [Java `ThreadLocal`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ThreadLocal.html)
