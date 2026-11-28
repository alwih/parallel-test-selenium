package io.github.alwih.selenium.steps;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// picocontainer builds one per scenario and hands the same instance to every step class that asks for it
public class ScenarioCart {

    // name -> list price of what the scenario has added, in the order the cart shows them
    public final Map<String, BigDecimal> added = new LinkedHashMap<>();
}
