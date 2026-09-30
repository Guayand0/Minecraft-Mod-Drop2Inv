package com.guayand0.containers;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.entities.EntityDropUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ContainerDropUtils {

    private ContainerDropUtils() {
    }

    public static boolean shouldHandleBlockContainer(BlockState state, BlockEntity blockEntity, Drop2InvConfig config) {
        if (!config.enabled || !config.containers.containers_to_inv) {
            return false;
        }
        if (!(blockEntity instanceof Container)) {
            return false;
        }
        return isConfiguredBlockContainer(state.getBlock(), config);
    }

    public static boolean shouldHandleEntityContainer(Entity entity, Drop2InvConfig config) {
        if (!config.enabled || !config.entities.entities_to_inv) {
            return false;
        }
        return isConfiguredEntityContainer(entity, config);
    }

    public static void transferBlockContainerContents(ServerLevel level, Player player, BlockPos pos, Container container) {
        transferContainerContents(level, player, pos, container);
    }

    public static void transferEntityContainerContents(ServerLevel level, Player player, Entity entity, Container container) {
        transferContainerContents(level, player, entity.blockPosition(), container);
    }

    public static void transferArmorStandContents(ServerLevel level, Player player, ArmorStand armorStand) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = armorStand.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack toGive = stack.copy();
            player.getInventory().add(toGive);
            armorStand.setItemSlot(slot, ItemStack.EMPTY);
            if (!toGive.isEmpty()) {
                Block.popResource(level, armorStand.blockPosition(), toGive);
            }
        }
    }

    public static boolean isConfiguredBlockContainer(Block block, Drop2InvConfig config) {
        Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
        if (identifier == null) {
            return false;
        }

        String path = identifier.getPath();
        return (config.containers.chest && path.equals("chest"))
                || (config.containers.trapped_chest && path.equals("trapped_chest"))
                || (config.containers.copper_chests && path.contains("copper_chest"))
                || (config.containers.barrel && path.equals("barrel"))
                || (config.containers.dropper && path.equals("dropper"))
                || (config.containers.dispenser && path.equals("dispenser"))
                || (config.containers.furnace && path.equals("furnace"))
                || (config.containers.smoker && path.equals("smoker"))
                || (config.containers.blast_furnace && path.equals("blast_furnace"))
                || (config.containers.hopper && path.equals("hopper"))
                || (config.containers.crafter && path.equals("crafter"))
                || (config.containers.decorated_pot && path.equals("decorated_pot"))
                || (config.containers.jukebox && path.equals("jukebox"))
                || (config.containers.brewing_stand && path.equals("brewing_stand"))
                || (config.containers.bookshelf && (path.equals("bookshelf") || path.equals("chiseled_bookshelf")))
                || (config.containers.shelves && path.endsWith("_shelf"));
    }

    public static boolean isConfiguredEntityContainer(Entity entity, Drop2InvConfig config) {
        return EntityDropUtils.isConfiguredChestBoat(entity, config)
                || EntityDropUtils.isConfiguredSpecialMinecart(entity, config)
                || EntityDropUtils.isConfiguredArmorStand(entity, config)
                || EntityDropUtils.isConfiguredItemFrame(entity, config);
    }

    private static void transferContainerContents(ServerLevel level, Player player, BlockPos pos, Container container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.removeItemNoUpdate(slot);
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack toGive = stack.copy();
            player.getInventory().add(toGive);
            if (!toGive.isEmpty()) {
                Block.popResource(level, pos, toGive);
            }
        }
    }
}
