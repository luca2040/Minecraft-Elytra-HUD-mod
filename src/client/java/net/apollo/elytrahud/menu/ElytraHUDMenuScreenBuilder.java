package net.apollo.elytrahud.menu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.apollo.elytrahud.ElytraHUDClient;
import net.apollo.elytrahud.config.load_save;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ElytraHUDMenuScreenBuilder {

    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.elytrahud.config"));

        builder.setSavingRunnable(load_save::save);

        ConfigCategory hud = builder.getOrCreateCategory(Text.translatable("category.elytrahud.HUD"));
        ConfigCategory message = builder.getOrCreateCategory(Text.translatable("category.elytrahud.message"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ################### HUD ###################
        hud.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("option.elytrahud.showHUD"), ElytraHUDClient.showDurability)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> ElytraHUDClient.showDurability = newValue)
                        .build());

        // POSITION
        hud.addEntry(
                entryBuilder.startIntSlider(Text.translatable("option.elytrahud.position"), ElytraHUDClient.position, 0, 7)
                        .setDefaultValue(6)
                        .setTooltip(Text.translatable("option.tooltip.elytrahud.position"))
                        .setSaveConsumer(newValue -> ElytraHUDClient.position = newValue)
                        .build());

        hud.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("option.elytrahud.showPercent"), ElytraHUDClient.HUD_show_percent)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("option.tooltip.elytrahud.showPercent"))
                        .setSaveConsumer(newValue -> ElytraHUDClient.HUD_show_percent = newValue)
                        .build());

        // ################### message ###################

        message.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("option.elytrahud.showMessage"), ElytraHUDClient.showWarning)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> ElytraHUDClient.showWarning = newValue)
                        .build());

        message.addEntry(
                entryBuilder.startStrField(Text.translatable("option.elytrahud.messageText"), ElytraHUDClient.messageText)
                        .setDefaultValue("Elytra is about to break!")
                        .setSaveConsumer(newValue -> ElytraHUDClient.messageText = newValue)
                        .build());

        message.addEntry(
                entryBuilder.startColorField(Text.translatable("option.elytrahud.messageColor"), ElytraHUDClient.message_color)
                        .setDefaultValue(12451840)
                        .setSaveConsumer(newValue -> ElytraHUDClient.message_color = newValue)
                        .build());

        return builder.build();
    }

}
