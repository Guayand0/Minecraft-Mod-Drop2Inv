package com.guayand0.blocks;

import com.guayand0.blocks.utils.MushroomUtils;
import com.guayand0.blocks.utils.DropUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class GiantMushroomBreakHandler {

    public static void breakMushroom(ServerLevel world, Player player, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> stems = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);

        // BFS solo con adyacentes para tallos
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;

            BlockState state = world.getBlockState(pos);
            if (!MushroomUtils.isMushroomStem(state.getBlock())) continue;

            stems.add(pos);

            for (BlockPos adj : getAdjacent(pos)) {
                if (!visited.contains(adj)) queue.add(adj);
            }
        }

        // Romper tallos de arriba hacia abajo
        stems.stream()
                .sorted((a, b) -> Integer.compare(b.getY(), a.getY()))
                .forEach(pos -> {
                    BlockState state = world.getBlockState(pos);
                    DropUtils.breakBlockToInventory(world, player, pos, state, null);
                });

        // Romper capuchas conectadas
        MushroomCapBreakHandler.breakCaps(world, player, stems);
    }

    // 6 adyacentes
    private static List<BlockPos> getAdjacent(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west());
    }
}
