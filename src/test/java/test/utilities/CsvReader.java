package test.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reusable CSV reader for test data stored under src/test/resources. */
public final class CsvReader {
    private CsvReader() {}

    public static List<Map<String, String>> read(String resourceName) {
        try (InputStream input = CsvReader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalArgumentException("CSV resource not found: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String headerLine = reader.readLine();
                if (headerLine == null || headerLine.isBlank()) {
                    throw new IllegalArgumentException("CSV is empty: " + resourceName);
                }

                List<String> headers = parseLine(headerLine);
                List<Map<String, String>> rows = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    List<String> values = parseLine(line);
                    if (values.size() != headers.size()) {
                        throw new IllegalArgumentException(
                                "Invalid CSV row in " + resourceName + ": " + line);
                    }
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        row.put(headers.get(i).trim(), values.get(i).trim());
                    }
                    rows.add(row);
                }
                return rows;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read CSV: " + resourceName, e);
        }
    }

    private static List<String> parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        return values;
    }
}
