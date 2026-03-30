package com.guayand0.fabric;

import com.guayand0.blocks.CommonBlockBreakHooks;
import com.guayand0.blocks.CommonItemEntityHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

public final class FabricDrop2InvBootstrap {

    private FabricDrop2InvBootstrap() {
    }

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return true;
            }

            return !CommonBlockBreakHooks.shouldCancelBreak(serverLevel, serverPlayer, pos, state, blockEntity);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ItemEntity itemEntity && CommonItemEntityHooks.shouldDiscard(itemEntity)) {
                itemEntity.discard();
            }
        });
    }
}
