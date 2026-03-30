package com.guayand0.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Drop2InvConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path configPath;
    private static Drop2InvConfig config;

    private Drop2InvConfigManager() {
    }

    public static void init(Path configDir) {
        configPath = configDir.resolve("drop2inv.json");
    }

    public static void load() {
        ensureInitialized();

        try {
            Files.createDirectories(configPath.getParent());
            if (Files.notExists(configPath)) {
                config = copyDefaults();
                save();
                return;
            }

            Drop2InvConfig loaded = GSON.fromJson(Files.readString(configPath), Drop2InvConfig.class);
            config = loaded != null ? loaded : copyDefaults();
        } catch (Exception e) {
            config = copyDefaults();
            e.printStackTrace();
        }
    }

    public static void save() throws IOException {
        ensureInitialized();
        if (config == null) {
            config = copyDefaults();
        }

        Files.createDirectories(configPath.getParent());
        Files.writeString(configPath, GSON.toJson(config));
    }

    public static Drop2InvConfig get() {
        if (config == null) {
            config = copyDefaults();
        }
        return config;
    }

    private static void ensureInitialized() {
        if (configPath == null) {
            throw new IllegalStateException("Drop2InvConfigManager.init must be called before use.");
        }
    }

    private static Drop2InvConfig copyDefaults() {
        return GSON.fromJson(GSON.toJson(Drop2InvConfig.DEFAULTS), Drop2InvConfig.class);
    }
}
