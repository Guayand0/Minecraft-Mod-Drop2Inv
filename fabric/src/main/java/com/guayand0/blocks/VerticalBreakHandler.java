package com.guayand0.blocks;

import com.guayand0.blocks.utils.DropUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class VerticalBreakHandler {

    public static boolean isVerticalBlock(Block block) {
        return block == Blocks.BAMBOO ||
                block == Blocks.SUGAR_CANE ||
                block == Blocks.CACTUS ||
                block == Blocks.KELP_PLANT;
    }

    public static void breakVertical(ServerLevel world, Player player, BlockPos pos, Block type) {
        BlockPos current = pos;

        while (true) {
            BlockState state = world.getBlockState(current);
            Block block = state.getBlock();

            if (type == Blocks.KELP_PLANT) {
                if (block != Blocks.KELP && block != Blocks.KELP_PLANT) break;
            } else {
                if (block != type) break;
            }

            DropUtils.breakBlockToInventory(world, player, current, state, null);
            current = current.above();
        }
    }

    public static void breakChorus(ServerLevel world, Player player, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Stack<BlockPos> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();
            if (!visited.add(pos)) continue;

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != Blocks.CHORUS_PLANT) continue;

            BlockPos above = pos.above();
            stack.push(above);

            for (Direction d : Direction.Plane.HORIZONTAL) {
                stack.push(above.relative(d));
            }

            DropUtils.breakBlockToInventory(world, player, pos, state, null);
        }
    }
}
