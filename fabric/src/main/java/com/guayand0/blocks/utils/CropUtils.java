package com.guayand0.blocks.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CropUtils {

    public static void giveCropDrops(ServerLevel world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {

        ItemStack tool = player.getMainHandItem();
        List<ItemStack> drops = Block.getDrops(state, world, pos, blockEntity, player, tool);

        for (ItemStack stack : drops) {
            player.getInventory().add(stack);
        }
    }
}
