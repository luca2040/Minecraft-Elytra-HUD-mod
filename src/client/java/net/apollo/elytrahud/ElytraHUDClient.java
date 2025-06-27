package net.apollo.elytrahud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

import static net.apollo.elytrahud.config.load_save.load;

public class ElytraHUDClient implements ClientModInitializer {
    //#################  CONFIG  #########################

    // HUD
    public static boolean showDurability = true;
    public static int position = 6;
    public static boolean HUD_show_percent = true;

    // message
    public static boolean showWarning = true;
    public static String messageText = "Elytra is about to break!";
    public static int message_color = 12451840;

    //####################################################
    private static final DecimalFormat df = new DecimalFormat("###.#");

    private String durability = "0 %";
    private boolean show = false;
    private final float minPerc = 0.046f;

    public static final String config_file_path = FabricLoader.getInstance().getConfigDir().toAbsolutePath() + "\\ElytraHUD_config.toml";

    @Override
    public void onInitializeClient() {
        load();

        ClientTickEvents.START_CLIENT_TICK.register((MinecraftClient client) -> {

            ClientPlayerEntity player = client.player;

            if (player != null) {

                boolean isCreative = player.isCreative();
                boolean hiddenHUD = client.options.hudHidden;

                if (!isCreative && !hiddenHUD) {
                    ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);

                    boolean hasElytra = chestStack.getItem().toString().equals("minecraft:elytra");

                    if (hasElytra) {
                        int itemMaxDamage = chestStack.getMaxDamage();

                        int dmg = itemMaxDamage - chestStack.getDamage();
                        float durability_float = (dmg == 1) ? 0 : (float) dmg / itemMaxDamage;

                        if (HUD_show_percent) {
                            durability = df.format(durability_float * 100) + " %";
                        } else {
                            durability = dmg + " / " + itemMaxDamage;
                        }

                        boolean isPlayerFlying = player.getPose().toString().equals("FALL_FLYING");

                        if (isPlayerFlying && durability_float < minPerc && showWarning) {
                            player.sendMessage(
                                    Text.literal(messageText)
                                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(message_color))
                                                    .withBold(true)),
                                    true);
                        }

                        show = true;
                    } else {
                        show = false;
                    }
                } else {
                    show = false;
                }
            }

        });

        HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
            if (show && showDurability) {

                TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

                int width = context.getScaledWindowWidth();
                int height = context.getScaledWindowHeight();

                int renderX;
                int renderY;

                final int upperY = 22;
                final int centerY = height / 2;
                final int lowerY = height - 3;

                final int leftX = 5;
                final int rightX = width - 78;

                // X position
                switch (position) {
                    case 0, 2, 4 -> renderX = leftX;
                    case 1, 3, 7 -> renderX = rightX;
                    case 5 -> renderX = -172 + (width / 2);
                    default -> renderX = 92 + (width / 2);
                }
                // Y position
                switch (position) {
                    case 0, 1 -> renderY = upperY;
                    case 2, 3 -> renderY = centerY;
                    default -> renderY = lowerY;
                }

                Identifier texture = Identifier.of("minecraft", "textures/item/elytra.png");
                context.drawTexture(RenderLayer::getGuiTextured, texture, renderX + 5, renderY - 16, 0, 0, 16, 16, 16, 16);

                context.drawText(renderer, durability, renderX + 5 + 18, renderY - 16 + 4, 16777215, true);

            }
        });
    }
}