package net.apollo.elytrahud.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TomlReader {
    public static Map<String, Object> readToml(String filePath) throws IOException {
        Map<String, Object> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();

                    String valueString = parts[1].trim().replace("\"", "");
                    Object value;
                    if (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false")) {
                        value = Boolean.parseBoolean(valueString);
                    } else {
                        try {
                            value = Integer.parseInt(valueString);
                        } catch (NumberFormatException e) {
                            value = valueString;
                        }
                    }

                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
