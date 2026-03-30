package com.guayand0.blocks;

import com.guayand0.blocks.utils.DropUtils;
import com.guayand0.blocks.utils.MushroomUtils;
import com.guayand0.blocks.utils.TreeUtils;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class CommonBlockBreakHooks {

    private CommonBlockBreakHooks() {
    }

    public static boolean shouldCancelBreak(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (player.getAbilities().instabuild) {
            return false;
        }

        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.blocks.blocks_to_inv) {
            return false;
        }

        Block block = state.getBlock();

        if (config.blocks.break_tree_logs && TreeUtils.isLog(state)) {
            ItemStack held = player.getMainHandItem();
            if (held.getItem() instanceof AxeItem) {
                TreeBreakHandler.breakTree(level, player, pos);
                DropTracker.mark(pos);
                return true;
            }
        }

        if (config.blocks.break_giant_mushroom && MushroomUtils.isMushroomStem(block)) {
            GiantMushroomBreakHandler.breakMushroom(level, player, pos);
            DropTracker.mark(pos);
            return true;
        }

        if (config.blocks.break_chorus && block == Blocks.CHORUS_PLANT) {
            VerticalBreakHandler.breakChorus(level, player, pos);
            DropTracker.mark(pos);
            return true;
        }

        if (config.blocks.break_vertical && VerticalBreakHandler.isVerticalBlock(block)) {
            VerticalBreakHandler.breakVertical(level, player, pos, block);
            DropTracker.mark(pos);
            return true;
        }

        DropTracker.mark(pos);
        DropUtils.breakBlockToInventory(level, player, pos, state, blockEntity);
        return true;
    }
}
