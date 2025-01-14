package net.apollo.elytrahud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;


import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public static final boolean IS_TRINKETS_API_PRESENT;
    private static Method getTrinketComponentMethod;
    private static Method getEquippedMethod;

    public static final Predicate<ItemStack> ELYTRA_CHECK = stack -> stack.getItem() instanceof net.minecraft.item.ElytraItem;

    static {
        boolean isPresent;
        try {
            // Load Trinkets API if present
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            Class<?> trinketComponentClass = Class.forName("dev.emi.trinkets.api.TrinketComponent");

            getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", LivingEntity.class);
            getEquippedMethod = trinketComponentClass.getMethod("getEquipped", Predicate.class);

            isPresent = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            isPresent = false;
        }
        IS_TRINKETS_API_PRESENT = isPresent;
    }

    @Override
    public void onInitializeClient() {
        load();

        ClientTickEvents.START_CLIENT_TICK.register((MinecraftClient client) -> {

            ClientPlayerEntity player = client.player;

            if (player != null) {

                boolean isCreative = player.isCreative();
                boolean hiddenHUD = client.options.hudHidden;

                if (!isCreative && !hiddenHUD) {
                    PlayerInventory inv = player.getInventory();
                    ItemStack is = inv.getArmorStack(2);

                    boolean hasElytra = is.getItem() instanceof net.minecraft.item.ElytraItem;

                    if (!hasElytra && IS_TRINKETS_API_PRESENT) {
                        is = getTrinketsElytraItemStack(player);
                        hasElytra = is.getItem() instanceof net.minecraft.item.ElytraItem;
                    }

                    if (hasElytra) {
                        int dam = is.getMaxDamage() - is.getDamage();
                        float durability_float = (dam == 1) ? 0 : (float) dam / is.getMaxDamage();

                        if (HUD_show_percent) {
                            durability = df.format(durability_float * 100) + " %";
                        } else {
                            durability = dam + " / " + is.getMaxDamage();
                        }

                        if (player.isFallFlying() && durability_float < minPerc && showWarning) {
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

                switch (position) {
                    case 0, 2, 4 -> renderX = leftX;
                    case 1, 3, 7 -> renderX = rightX;
                    case 5 -> renderX = -172 + (width / 2);
                    default -> renderX = 92 + (width / 2);
                }
                switch (position) {
                    case 0, 1 -> renderY = upperY;
                    case 2, 3 -> renderY = centerY;
                    default -> renderY = lowerY;
                }

                Identifier texture = Identifier.of("minecraft", "textures/item/elytra.png");
                context.drawTexture(texture, renderX + 5, renderY - 16, 0, 0, 16, 16, 16, 16);

                context.drawText(renderer, durability, renderX + 5 + 18, renderY - 16 + 4, 16777215, true);

            }
        });
    }

    @SuppressWarnings("unchecked")
    public ItemStack getTrinketsElytraItemStack(LivingEntity livingEntity) {
        try {
            Optional<?> trinketComponentOpt = (Optional<?>) getTrinketComponentMethod.invoke(null, livingEntity);

            if (trinketComponentOpt.isPresent()) {
                Object trinketComponent = trinketComponentOpt.get();
                List<?> equippedList = (List<?>) getEquippedMethod.invoke(trinketComponent, ELYTRA_CHECK);

                if (!equippedList.isEmpty()) {
                    Pair<Object, ItemStack> firstPair = (Pair<Object, ItemStack>) equippedList.get(0);
                    return firstPair.getRight();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ItemStack.EMPTY;
    }
}