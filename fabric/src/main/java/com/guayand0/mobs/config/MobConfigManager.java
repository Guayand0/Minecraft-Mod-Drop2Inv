package com.guayand0.mobs.config;

import com.google.gson.Gson;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobConfigManager {

    private static MobConfigManager instance;

    private final List<String> passive = new ArrayList<>();
    private final List<String> neutral = new ArrayList<>();
    private final List<String> hostile = new ArrayList<>();

    private MobConfigManager() {}

    public static MobConfigManager get() {
        if (instance == null) {
            instance = new MobConfigManager();
        }
        return instance;
    }

    public void load(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (InputStream in = getClass().getClassLoader().getResourceAsStream("mobs.default.json")) {
                    Files.copy(in, path);
                }
            }

            MobConfigManager loaded = new Gson().fromJson(Files.readString(path), MobConfigManager.class);
            this.passive.clear();
            this.passive.addAll(loaded.passive);
            this.neutral.clear();
            this.neutral.addAll(loaded.neutral);
            this.hostile.clear();
            this.hostile.addAll(loaded.hostile);

            // Rellenar categorías individuales si es necesario
            Drop2InvConfig cfg = Drop2InvConfigManager.get();
            if (cfg.mobs.individual_category == null) {
                cfg.mobs.individual_category = new HashMap<>();
            }
            passive.forEach(id -> cfg.mobs.individual_category.putIfAbsent(id, MobCategory.PASSIVE));
            neutral.forEach(id -> cfg.mobs.individual_category.putIfAbsent(id, MobCategory.NEUTRAL));
            hostile.forEach(id -> cfg.mobs.individual_category.putIfAbsent(id, MobCategory.HOSTILE));

        } catch (Exception e) {
            throw new RuntimeException("Error loading mobs.json", e);
        }
    }

    public List<String> getPassive() { return passive; }
    public List<String> getNeutral() { return neutral; }
    public List<String> getHostile() { return hostile; }

}