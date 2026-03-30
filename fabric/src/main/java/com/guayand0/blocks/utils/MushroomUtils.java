package com.guayand0.blocks.utils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MushroomUtils {

    public static boolean isMushroomStem(Block block) {
        return block == Blocks.MUSHROOM_STEM;
    }

    public static boolean isMushroomCap(Block block) {
        return block == Blocks.RED_MUSHROOM_BLOCK || block == Blocks.BROWN_MUSHROOM_BLOCK;
    }

    public static boolean isRed(Block block) {
        return block == Blocks.RED_MUSHROOM_BLOCK;
    }

    public static boolean isBrown(Block block) {
        return block == Blocks.BROWN_MUSHROOM_BLOCK;
    }

}
