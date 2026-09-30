package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.containers.ContainerDropUtils;
import com.guayand0.entities.EntityDropUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void drop2inv$transferEquipment(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        ArmorStand armorStand = (ArmorStand) (Object) this;
        if (!ContainerDropUtils.shouldHandleEntityContainer(armorStand, config)) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        EntityDropUtils.giveStackToPlayer(level, player, armorStand.blockPosition(), armorStand.getPickResult());
        ContainerDropUtils.transferArmorStandContents(level, player, armorStand);
        armorStand.discard();
        cir.setReturnValue(true);
    }
}
