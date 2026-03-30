package com.guayand0.blocks.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DropUtils {

    public static void giveDrops(ServerLevel world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {

        ItemStack tool = player.getMainHandItem();

        if (state.requiresCorrectToolForDrops() && !tool.isCorrectToolForDrops(state)) return;

        LootParams.Builder lootBuilder = new LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player);

        List<ItemStack> drops = state.getDrops(lootBuilder);

        for (ItemStack stack : drops) {
            player.getInventory().add(stack);
        }
    }

    public static void breakBlockToInventory(ServerLevel world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack tool = player.getMainHandItem();

        giveDrops(world, player, pos, state, blockEntity);
        if (world.destroyBlock(pos, false)) {
            tool.mineBlock(world, state, pos, player);
        }
    }
}
