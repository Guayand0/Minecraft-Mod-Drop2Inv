package com.guayand0.blocks;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.blocks.utils.DropUtils;
import com.guayand0.blocks.utils.TreeUtils;
import com.guayand0.config.Drop2InvConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class TreeBreakHandler {

    public static void breakTree(ServerLevel world, Player player, BlockPos start) {

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        Drop2InvConfig config = Drop2InvConfigManager.get();
        ItemStack axe = player.getMainHandItem();

        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> logs = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;

            BlockState state = world.getBlockState(pos);
            if (!TreeUtils.isLog(state)) continue;

            logs.add(pos);

            for (int x = -1; x <= 1; x++)
                for (int y = -1; y <= 1; y++)
                    for (int z = -1; z <= 1; z++)
                        queue.add(pos.offset(x, y, z));
        }

        logs.stream()
                .sorted((a, b) -> Integer.compare(b.getY(), a.getY()))
                .forEach(pos -> {
                    if (axe.isEmpty()) return;

                    BlockState state = world.getBlockState(pos);

                    DropUtils.breakBlockToInventory(world, player, pos, state, null);
                });


        if (config.blocks.break_tree_leaf) {
            LeafBreakHandler.breakLeavesFromLogs(world, player, logs);
        }
    }
}
