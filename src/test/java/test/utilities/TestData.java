package test.utilities;

import java.util.List;
import java.util.Map;

public final class TestData {

    public static List<Map<String, String>> api() {
        return CsvReader.read("api-data.csv");
    }

    public static Map<String, String> mobile() {
        return CsvReader.read("mobile-data.csv").stream()
                .filter(row -> "mobile".equalsIgnoreCase(row.get("section")))
                .collect(java.util.stream.Collectors.toMap(
                        row -> row.get("key"),
                        row -> row.get("value"),
                        (first, ignored) -> first,
                        java.util.LinkedHashMap::new));
    }

    public static String get(String section, String key, String defaultValue) {
        return CsvReader.read("mobile-data.csv").stream()
                .filter(row -> section.equalsIgnoreCase(row.getOrDefault("section", "")))
                .filter(row -> key.equalsIgnoreCase(row.getOrDefault("key", "")))
                .map(row -> row.get("value"))
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse(defaultValue);
    }
}
