package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.containers.ContainerDropUtils;
import com.guayand0.entities.EntityDropUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void drop2inv$transferFrame(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        ItemFrame itemFrame = (ItemFrame) (Object) this;
        if (!ContainerDropUtils.shouldHandleEntityContainer(itemFrame, config)) {
            return;
        }

        Entity sourceEntity = damageSource.getEntity();
        if (!(sourceEntity instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        if (!itemFrame.getItem().isEmpty()) {
            ItemStack item = itemFrame.getItem().copy();
            EntityDropUtils.giveStackToPlayer(level, player, itemFrame.blockPosition(), item);
            itemFrame.setItem(ItemStack.EMPTY, false);
            cir.setReturnValue(true);
            return;
        }

        ItemStack frameItem = itemFrame instanceof GlowItemFrame ? new ItemStack(Items.GLOW_ITEM_FRAME) : new ItemStack(Items.ITEM_FRAME);
        EntityDropUtils.giveStackToPlayer(level, player, itemFrame.blockPosition(), frameItem);
        itemFrame.discard();
        cir.setReturnValue(true);
    }
}
