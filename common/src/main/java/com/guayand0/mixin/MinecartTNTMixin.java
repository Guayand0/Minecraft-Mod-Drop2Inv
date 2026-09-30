package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.entities.EntityDropUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartTNT.class)
public abstract class MinecartTNTMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void drop2inv$transferDrops(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.entities.entities_to_inv || !config.entities.special_minecarts) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        MinecartTNT minecart = (MinecartTNT) (Object) this;
        EntityDropUtils.giveStackToPlayer(level, player, minecart.blockPosition(), minecart.getPickResult());
        minecart.discard();
        cir.setReturnValue(true);
    }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void drop2inv$transferDrops(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.entities.entities_to_inv || !config.entities.special_minecarts) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        MinecartTNT minecart = (MinecartTNT) (Object) this;
        EntityDropUtils.giveStackToPlayer(level, player, minecart.blockPosition(), minecart.getPickResult());
        minecart.discard();
        ci.cancel();
    }
}
