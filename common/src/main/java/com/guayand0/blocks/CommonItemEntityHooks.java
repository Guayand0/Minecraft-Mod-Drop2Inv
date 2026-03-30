package com.guayand0.blocks;

import net.minecraft.world.entity.item.ItemEntity;

public final class CommonItemEntityHooks {

    private CommonItemEntityHooks() {
    }

    public static boolean shouldDiscard(ItemEntity itemEntity) {
        return DropTracker.consume(itemEntity.blockPosition());
    }
}
