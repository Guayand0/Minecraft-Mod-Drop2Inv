package com.guayand0.mobs.logic;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

public class MobDropLogic {

    /**
     * Da los ítems al jugador directamente según la categoría del mob
     */
    public static void give(ServerPlayer player, ItemEntity item, MobCategory category) {
        if (give(player, item.getItem(), category)) {
            item.discard();
        }
    }

    public static boolean give(ServerPlayer player, net.minecraft.world.item.ItemStack stack, MobCategory category) {

        Drop2InvConfig config = Drop2InvConfigManager.get();

        boolean allow;

        switch (category) {
            case HOSTILE -> allow = config.mobs.hostile;
            case NEUTRAL -> allow = config.mobs.neutral;
            case PASSIVE -> allow = config.mobs.passive;
            default -> {
                return false;
            }
        }

        if (!allow) return false; // deja caer vanilla

        return player.getInventory().add(stack);
    }

}
