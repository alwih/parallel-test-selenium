package io.github.alwih.selenium.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Config {

    private static final String FILE_NAME = "config.properties";
    static final Properties FILE = load();

    private Config() {
    }

    public static String get(String key) {
        return get(key, System::getProperty, System::getenv, FILE::getProperty);
    }

    static String get(String key, Function<String, String> systemProperties,
            Function<String, String> env, Function<String, String> file) {
        String envName = key.toUpperCase(Locale.ROOT).replace('.', '_');
        return Stream.of(systemProperties.apply(key), env.apply(envName), file.apply(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing setting '" + key + "': pass -D"
                        + key + "=..., set env " + envName + " or add it to " + FILE_NAME));
    }

    private static Properties load() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException(FILE_NAME + " not found on the classpath (src/test/resources)");
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
