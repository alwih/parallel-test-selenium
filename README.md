# Parallel Selenium Testing

A Selenium WebDriver project in Java that runs several browsers at the same time with **TestNG**, built with **Maven**.

Each test thread gets its own WebDriver through a `ThreadLocal`, so Chrome, Firefox and Edge sessions run side by side without sharing state. The suite opens three public websites in each browser and checks their page titles: nine browser sessions in parallel.

## Project Structure

```
parallel-test-selenium/
├── pom.xml                                # Maven build: Selenium 4.24, TestNG 7.10, Java 21
├── testng.xml                             # Suite: 9 tests (3 browsers × 3 sites) run in parallel
└── src/test/java/
    ├── runner/
    │   └── TestRunner.java                # Opens a site and checks its page title
    └── webdriver/
        └── WebDriverManager.java          # One WebDriver per thread (ThreadLocal) behind a singleton
```

## Prerequisites

- **JDK 21 or later** (the build compiles for Java 21)
- **Maven 3.6.3+** (required by the compiler and Surefire plugins)
- **Google Chrome, Firefox and Microsoft Edge** installed. Selenium Manager, part of Selenium 4, downloads the matching drivers on the first run
- **Git** and an **internet connection** (the tests open live websites)

## Setup Instructions

### 1. Install a JDK and Maven

On macOS with Homebrew:

```bash
brew install --cask temurin@21
brew install maven
```

Check that Maven runs on JDK 21 or later:

```bash
mvn -v
```

### 2. Clone the Repository

```bash
git clone https://github.com/alwih/parallel-test-selenium.git
cd parallel-test-selenium
```

### 3. Install Dependencies

Maven downloads Selenium and TestNG on the first build:

```bash
mvn test-compile
```

## Running the Tests

```bash
mvn test
```

Maven Surefire runs the suite in [testng.xml](testng.xml). It uses `parallel="tests"` with `thread-count="9"`, so every `<test>` block runs in its own thread with its own browser, all at the same time. Each thread prints its name and the page title, which shows the sessions running side by side.

## What Gets Tested

Each site is opened in Chrome, Firefox and Edge (9 tests), and the page title must match exactly:

| Test method | Site | Expected title |
|---|---|---|
| `launchGoogle` | https://google.com | `Google` |
| `launchBitbucket` | https://bitbucket.org | `Bitbucket \| Git solution for teams using Jira` |
| `launchTokopedia` | https://www.tokopedia.com | `Situs Jual Beli Online Terlengkap, Mudah & Aman \| Tokopedia` |

## Modifying Test Data

- **Browser and site combinations** live in [testng.xml](testng.xml). Each `<test>` block sets a `browser` parameter and includes one test method. Add or remove blocks to change the combinations, and keep `thread-count` equal to the number of blocks to run them all at once.
- **Browser values**: `chrome`, `firefox`, `edge` and `safari`. Any other value falls back to Chrome. Safari runs only one session at a time, so it isn't in the parallel suite.
- **URLs and expected titles** live in [TestRunner.java](src/test/java/runner/TestRunner.java).

## Troubleshooting

**`Fatal error compiling: error: invalid target release: 21`?**
- Maven is running on a JDK older than 21. Check with `mvn -v`, then install JDK 21 or later and point `JAVA_HOME` at it.

**The Firefox or Edge tests fail to start?**
- That browser isn't installed. Install it, or remove its `<test>` blocks from `testng.xml`.

**A title check fails?**
- These are live third-party sites, and their titles change. Open the site, copy the current title and update the expected value in `TestRunner.java`.

## Next Steps

- Build with Java 17 and add the Maven wrapper
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
- [Java `ThreadLocal`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html)
