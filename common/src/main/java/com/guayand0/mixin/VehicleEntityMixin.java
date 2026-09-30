package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.containers.ContainerDropUtils;
import com.guayand0.entities.EntityDropUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VehicleEntity.class)
public abstract class VehicleEntityMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void drop2inv$directVehicleDrops(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        Entity vehicle = (Entity) (Object) this;
        boolean handled =
                EntityDropUtils.isConfiguredBoat(vehicle, config)
                        || EntityDropUtils.isConfiguredChestBoat(vehicle, config)
                        || EntityDropUtils.isConfiguredMinecart(vehicle, config)
                        || EntityDropUtils.isConfiguredSpecialMinecart(vehicle, config);

        if (!handled || !config.entities.entities_to_inv) {
            return;
        }

        EntityDropUtils.giveStackToPlayer(level, player, vehicle.blockPosition(), vehicle.getPickResult());

        if (vehicle instanceof Container container) {
            ContainerDropUtils.transferEntityContainerContents(level, player, vehicle, container);
        }

        vehicle.discard();
        cir.setReturnValue(true);
    }
}
