package com.guayand0.mixin;

import com.guayand0.blocks.utils.DropUtils;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void drop2inv$giveDropsToInventory(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.blocks.blocks_to_inv || this.player.getAbilities().instabuild) {
            return;
        }

        BlockState state = this.level.getBlockState(pos);
        if (state.isAir()) {
            cir.setReturnValue(false);
            return;
        }

        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        DropUtils.giveDrops(this.level, this.player, pos, state, blockEntity);
        this.player.getMainHandItem().mineBlock(this.level, state, pos, this.player);
        this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        cir.setReturnValue(true);
    }
}
