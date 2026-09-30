package com.guayand0.entities;

import com.guayand0.config.Drop2InvConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class EntityDropUtils {

    private EntityDropUtils() {
    }

    public static void giveStackToPlayer(ServerLevel level, Player player, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack toGive = stack.copy();
        player.getInventory().add(toGive);
        if (!toGive.isEmpty()) {
            Block.popResource(level, pos, toGive);
        }
    }

    public static boolean isConfiguredBoat(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (identifier == null) {
            return false;
        }

        String path = identifier.getPath();
        return config.entities.boats && ((path.endsWith("_boat")) || (path.endsWith("_raft")));
    }

    public static boolean isConfiguredChestBoat(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (identifier == null) {
            return false;
        }

        String path = identifier.getPath();
        return config.entities.chest_boats && (path.endsWith("_chest_boat") || path.endsWith("_chest_raft"));
    }

    public static boolean isConfiguredMinecart(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (identifier == null) {
            return false;
        }

        return config.entities.minecarts && identifier.getPath().equals("minecart");
    }

    public static boolean isConfiguredSpecialMinecart(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (identifier == null) {
            return false;
        }

        if (!config.entities.special_minecarts) {
            return false;
        }

        String path = identifier.getPath();
        return path.equals("chest_minecart")
                || path.equals("hopper_minecart")
                || path.equals("furnace_minecart")
                || path.equals("tnt_minecart");
    }

    public static boolean isConfiguredArmorStand(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return identifier != null && config.entities.armor_stand && identifier.getPath().equals("armor_stand");
    }

    public static boolean isConfiguredItemFrame(Entity entity, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (identifier == null || !config.entities.item_frames) {
            return false;
        }

        String path = identifier.getPath();
        return path.equals("item_frame") || path.equals("glow_item_frame");
    }
}
