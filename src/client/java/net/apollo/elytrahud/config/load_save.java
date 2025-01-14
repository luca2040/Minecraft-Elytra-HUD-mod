package net.apollo.elytrahud.config;

import net.apollo.elytrahud.ElytraHUDClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.apollo.elytrahud.config.TomlReader.readToml;
import static net.apollo.elytrahud.config.TomlWriter.writeToml;

public class load_save {
    public static void save() {
        Map<String, Object> config_save_data = new HashMap<>();
        config_save_data.put("show_durability", ElytraHUDClient.showDurability);
        config_save_data.put("position", ElytraHUDClient.position);
        config_save_data.put("show_percent", ElytraHUDClient.HUD_show_percent);
        config_save_data.put("show_warning", ElytraHUDClient.showWarning);
        config_save_data.put("message_text", ElytraHUDClient.messageText);
        config_save_data.put("message_color", ElytraHUDClient.message_color);

        try {
            writeToml(ElytraHUDClient.config_file_path, config_save_data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            Map<String, Object> tomlData = readToml(ElytraHUDClient.config_file_path);
            for (Map.Entry<String, Object> entry : tomlData.entrySet()) {
                switch (entry.getKey()) {
                    case "show_durability" -> ElytraHUDClient.showDurability = (boolean) entry.getValue();
                    case "position" -> ElytraHUDClient.position = (int) entry.getValue();
                    case "show_percent" -> ElytraHUDClient.HUD_show_percent = (boolean) entry.getValue();
                    case "show_warning" -> ElytraHUDClient.showWarning = (boolean) entry.getValue();
                    case "message_text" -> ElytraHUDClient.messageText = (String) entry.getValue();
                    case "message_color" -> ElytraHUDClient.message_color = (int) entry.getValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
