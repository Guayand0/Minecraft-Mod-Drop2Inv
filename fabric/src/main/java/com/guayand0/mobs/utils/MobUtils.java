package com.guayand0.mobs.utils;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.config.MobConfigManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

public class MobUtils {

    public static MobCategory getCategory(EntityType<?> type) {
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        Drop2InvConfig config = Drop2InvConfigManager.get();
        MobConfigManager json = MobConfigManager.get();

        // Override por usuario
        if (config.mobs.individual_category.containsKey(mobId)) {
            return config.mobs.individual_category.get(mobId);
        }

        // Default
        return getDefaultCategory(mobId, json);
    }

    public static MobCategory getDefaultCategory(String mobId, MobConfigManager json) {
        json = MobConfigManager.get();
        if (json.getPassive().contains(mobId)) return MobCategory.PASSIVE;
        if (json.getNeutral().contains(mobId)) return MobCategory.NEUTRAL;
        if (json.getHostile().contains(mobId)) return MobCategory.HOSTILE;
        return MobCategory.NEUTRAL;
    }

    // Último jugador que interactuó con un mob “especial”
    public static ServerPlayer lastInteractor;
}
