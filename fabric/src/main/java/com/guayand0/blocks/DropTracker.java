package com.guayand0.blocks;

import net.minecraft.core.BlockPos;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DropTracker {

    private static final Set<BlockPos> BROKEN = ConcurrentHashMap.newKeySet();

    public static void mark(BlockPos pos) {
        BROKEN.add(pos.immutable());
    }

    public static boolean consume(BlockPos pos) {
        return BROKEN.remove(pos);
    }

    public static void clear() {
        BROKEN.clear();
    }

}
