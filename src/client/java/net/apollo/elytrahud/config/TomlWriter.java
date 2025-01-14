package net.apollo.elytrahud.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class TomlWriter {
    public static void writeToml(String filePath, Map<String, Object> map) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String valueString;

                if (value instanceof String) {
                    valueString = "\"" + value + "\"";
                } else {
                    valueString = value.toString();
                }

                bw.write(key + " = " + valueString);
                bw.newLine();
            }
        }
    }
}
