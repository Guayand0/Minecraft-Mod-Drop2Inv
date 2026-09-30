package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.entities.EntityDropUtils;
import com.guayand0.containers.ContainerDropUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartContainer.class)
public abstract class AbstractMinecartContainerMixin {

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void drop2inv$transferContents(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        AbstractMinecartContainer container = (AbstractMinecartContainer) (Object) this;
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!ContainerDropUtils.shouldHandleEntityContainer(container, config)) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        EntityDropUtils.giveStackToPlayer(level, player, container.blockPosition(), container.getPickResult());
        ContainerDropUtils.transferEntityContainerContents(level, player, container, container);
        container.discard();
        ci.cancel();
    }
}
