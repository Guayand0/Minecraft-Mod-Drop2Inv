package com.guayand0;

import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.config.MobConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class Drop2InvCommon {

    public static final String MOD_ID = "drop2inv";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private Drop2InvCommon() {
    }

    public static void init(Path configDir) {
        Drop2InvConfigManager.init(configDir);
        Drop2InvConfigManager.load();
    }

    public static void initClient(Path configDir) {
        init(configDir);
        MobConfigManager.get().load(configDir.resolve(MOD_ID).resolve("mobs.json"));
    }
}
