package com.guayand0.blocks;

import com.guayand0.blocks.utils.MushroomUtils;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.blocks.utils.CropUtils;
import com.guayand0.blocks.utils.DropUtils;
import com.guayand0.blocks.utils.TreeUtils;
import com.guayand0.config.Drop2InvConfigManager;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockBreakHandler {

    public static void register() {

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {

            if (world.isClientSide() || !(world instanceof ServerLevel serverWorld)) return true;
            if (player.getAbilities().instabuild) return true;

            Drop2InvConfig config = Drop2InvConfigManager.get();
            if (!config.enabled || !config.blocks.blocks_to_inv) return true;

            Block block = state.getBlock();

            // CROPS
            /*if (config.blocks.break_crops && block instanceof CropBlock) {
                CropUtils.giveCropDrops(serverWorld, player, pos, state, blockEntity);
                DropTracker.mark(pos);
                serverWorld.breakBlock(pos, false);
                return false;
            }*/

            // LOGS
            if (config.blocks.break_tree_logs && TreeUtils.isLog(state)) {
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof AxeItem) {
                    TreeBreakHandler.breakTree(serverWorld, player, pos);
                    DropTracker.mark(pos);
                    return false;
                }
            }

            // GIANT MUSHROOMS
            if (config.blocks.break_giant_mushroom && MushroomUtils.isMushroomStem(block)) {
                GiantMushroomBreakHandler.breakMushroom(serverWorld, player, pos);
                DropTracker.mark(pos);
                return false;
            }

            // CHORUS
            if (config.blocks.break_chorus && block == Blocks.CHORUS_PLANT) {
                VerticalBreakHandler.breakChorus(serverWorld, player, pos);
                DropTracker.mark(pos);
                return false;
            }

            // VERTICAL
            if (config.blocks.break_vertical && VerticalBreakHandler.isVerticalBlock(block)) {
                VerticalBreakHandler.breakVertical(serverWorld, player, pos, block);
                DropTracker.mark(pos);
                return false;
            }

            // BLOQUE NORMAL
            DropTracker.mark(pos);
            DropUtils.breakBlockToInventory(serverWorld, player, pos, state, blockEntity);
            return false;
        });
    }
}
