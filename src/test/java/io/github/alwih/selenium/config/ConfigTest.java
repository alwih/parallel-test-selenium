package io.github.alwih.selenium.config;

import java.util.Map;
import java.util.function.Function;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigTest {

    private static final Function<String, String> UNSET = key -> null;

    @Test
    public void systemPropertyBeatsEnvAndFile() {
        String value = Config.get("headless",
                Map.of("headless", "from-d")::get,
                Map.of("HEADLESS", "from-env")::get,
                Map.of("headless", "from-file")::get);
        Assert.assertEquals(value, "from-d");
    }

    @Test
    public void envBeatsFile() {
        String value = Config.get("headless", UNSET,
                Map.of("HEADLESS", "from-env")::get,
                Map.of("headless", "from-file")::get);
        Assert.assertEquals(value, "from-env");
    }

    @Test
    public void fileIsUsedWhenNothingElseIsSet() {
        String value = Config.get("headless", UNSET, UNSET, Map.of("headless", "from-file")::get);
        Assert.assertEquals(value, "from-file");
    }

    @Test
    public void envNameIsTheKeyUpperCasedWithUnderscores() {
        String value = Config.get("base.url", UNSET, Map.of("BASE_URL", "from-env")::get, UNSET);
        Assert.assertEquals(value, "from-env");
    }

    @Test
    public void missingKeyFailsWithItsName() {
        IllegalStateException e = Assert.expectThrows(IllegalStateException.class,
                () -> Config.get("base.url", UNSET, UNSET, UNSET));
        Assert.assertTrue(e.getMessage().contains("'base.url'"), "Message without the key: " + e.getMessage());
    }

    @Test
    public void configFileProvidesHeadless() {
        Assert.assertNotNull(Config.FILE.getProperty("headless"), "headless is missing from config.properties");
    }
}
