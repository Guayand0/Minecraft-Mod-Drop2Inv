package com.guayand0.blocks.utils;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class TreeUtils {

    public static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }
    public static boolean isLeave(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }
}
