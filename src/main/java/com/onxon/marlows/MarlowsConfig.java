package com.onxon.marlows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class MarlowsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, String> paperItems = new LinkedHashMap<>();
    public Map<String, String> bjMaps = new LinkedHashMap<>();
    public Map<String, String> oddItems = new LinkedHashMap<>();
    public Map<String, String> evenItems = new LinkedHashMap<>();
    public String orItem = "minecraft:map";
    public String fiftyLeft = "minecraft:stone";
    public String fiftyRight = "minecraft:dirt";

    public static MarlowsConfig load() {
        Path path = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/marlows-crystal-optimiser.json");
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    MarlowsConfig loaded = GSON.fromJson(reader, MarlowsConfig.class);
                    if (loaded != null) return loaded;
                }
            }
        } catch (Exception e) {
            MarlowsClient.LOGGER.error("Could not load mod config", e);
        }
        MarlowsConfig config = new MarlowsConfig();
        for (int i = 1; i <= 9; i++) config.paperItems.put(String.valueOf(i), "minecraft:stone");
        for (String n : new String[]{"1/11","3","4","5","6","7","8","9","10"}) config.bjMaps.put(n, "minecraft:map");
        for (int i = 1; i <= 4; i++) {
            config.oddItems.put(String.valueOf(i), "minecraft:map");
            config.evenItems.put(String.valueOf(i), "minecraft:map");
        }
        config.save();
        return config;
    }

    public void save() {
        Path path = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/marlows-crystal-optimiser.json");
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) { GSON.toJson(this, writer); }
        } catch (Exception e) {
            MarlowsClient.LOGGER.error("Could not save mod config", e);
        }
    }

    public ItemStack stack(String itemId) {
        try {
            Identifier id = Identifier.tryParse(itemId);
            if (id == null || !Registries.ITEM.containsId(id)) return ItemStack.EMPTY;
            return new ItemStack(Registries.ITEM.get(id));
        } catch (Exception ignored) {
            return ItemStack.EMPTY;
        }
    }
}
