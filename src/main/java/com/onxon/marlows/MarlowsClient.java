package com.onxon.marlows;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MarlowsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Marlow's crystal optimiser");
    public static MarlowsConfig CONFIG;
    public static String activeSection = "paper";
    public static String activeVisualItem = null;
    public static String paperWinningSide = "left";
    private static final Set<UUID> changedEntities = new HashSet<>();

    private static KeyBinding openMenu, paperLeft, paperRight, paperReset;
    private static KeyBinding bjReset, fiftyLeft, fiftyRight, fiftyReset;
    private static KeyBinding oddKey, evenKey, orKey, oddReset;
    private static final String[] BJ_KEYS = {"1/11","3","4","5","6","7","8","9","10"};
    private static KeyBinding[] bjKeys;

    @Override
    public void onInitializeClient() {
        CONFIG = MarlowsConfig.load();
        String category = "key.categories.marlows";
        openMenu = key("key.marlows.menu", GLFW.GLFW_KEY_O, category);
        paperLeft = key("key.marlows.paper_left", GLFW.GLFW_KEY_UNKNOWN, category);
        paperRight = key("key.marlows.paper_right", GLFW.GLFW_KEY_UNKNOWN, category);
        paperReset = key("key.marlows.paper_reset", GLFW.GLFW_KEY_UNKNOWN, category);
        bjReset = key("key.marlows.bj_reset", GLFW.GLFW_KEY_UNKNOWN, category);
        fiftyLeft = key("key.marlows.fifty_left", GLFW.GLFW_KEY_UNKNOWN, category);
        fiftyRight = key("key.marlows.fifty_right", GLFW.GLFW_KEY_UNKNOWN, category);
        fiftyReset = key("key.marlows.fifty_reset", GLFW.GLFW_KEY_UNKNOWN, category);
        oddKey = key("key.marlows.odd", GLFW.GLFW_KEY_UNKNOWN, category);
        evenKey = key("key.marlows.even", GLFW.GLFW_KEY_UNKNOWN, category);
        orKey = key("key.marlows.or", GLFW.GLFW_KEY_UNKNOWN, category);
        oddReset = key("key.marlows.odd_reset", GLFW.GLFW_KEY_UNKNOWN, category);
        bjKeys = new KeyBinding[BJ_KEYS.length];
        for (int i = 0; i < BJ_KEYS.length; i++)
            bjKeys[i] = key("key.marlows.bj_" + BJ_KEYS[i].replace("/", "_"), GLFW.GLFW_KEY_UNKNOWN, category);

        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    private static KeyBinding key(String translation, int key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(translation, InputUtil.Type.KEYSYM, key, category));
    }

    private void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        if (openMenu.wasPressed()) client.setScreen(new MarlowsMenuScreen());

        if (paperLeft.wasPressed()) { activeSection = "paper"; paperWinningSide = "left"; activatePaper(); }
        if (paperRight.wasPressed()) { activeSection = "paper"; paperWinningSide = "right"; activatePaper(); }
        if (paperReset.wasPressed()) resetVisual();
        if (bjReset.wasPressed()) resetVisual();
        for (int i = 0; i < bjKeys.length; i++) {
            if (bjKeys[i].wasPressed()) {
                activeSection = "bj";
                activeVisualItem = CONFIG.bjMaps.get(BJ_KEYS[i]);
                changedEntities.clear();
            }
        }
        if (fiftyLeft.wasPressed()) { activeSection = "50/50"; activeVisualItem = CONFIG.fiftyLeft; changedEntities.clear(); }
        if (fiftyRight.wasPressed()) { activeSection = "50/50"; activeVisualItem = CONFIG.fiftyRight; changedEntities.clear(); }
        if (fiftyReset.wasPressed()) resetVisual();
        if (oddKey.wasPressed()) chooseRandom(CONFIG.oddItems);
        if (evenKey.wasPressed()) chooseRandom(CONFIG.evenItems);
        if (orKey.wasPressed()) { activeSection = "oddoreven"; activeVisualItem = CONFIG.orItem; changedEntities.clear(); }
        if (oddReset.wasPressed()) resetVisual();

        // Visual-only prototype: changes newly encountered dropped item entities near the player.
        // The server remains authoritative and may resend the original stack at any time.
        if (activeVisualItem != null) {
            Box area = client.player.getBoundingBox().expand(5.0);
            for (ItemEntity entity : client.world.getEntitiesByClass(ItemEntity.class, area, e -> true)) {
                if (entity.getUuid() == null || changedEntities.contains(entity.getUuid())) continue;
                ItemStack replacement = CONFIG.stack(activeVisualItem);
                if (!replacement.isEmpty()) {
                    replacement.setCount(Math.max(1, entity.getStack().getCount()));
                    entity.setStack(replacement);
                    changedEntities.add(entity.getUuid());
                }
            }
        }
    }

    private void activatePaper() {
        String best = CONFIG.paperItems.get("9");
        activeVisualItem = best;
        changedEntities.clear();
    }

    private void chooseRandom(java.util.Map<String, String> items) {
        activeSection = "oddoreven";
        java.util.List<String> values = items.values().stream().toList();
        if (!values.isEmpty()) activeVisualItem = values.get((int)(Math.random() * values.size()));
        changedEntities.clear();
    }

    public static void resetVisual() {
        activeVisualItem = null;
        changedEntities.clear();
    }

    public static void setVisualItem(String id) {
        activeVisualItem = id;
        changedEntities.clear();
    }

    public static String[] bjNumbers() { return BJ_KEYS; }
    public static KeyBinding openMenuBinding() { return openMenu; }
}
