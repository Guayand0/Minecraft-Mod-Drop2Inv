package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.logic.MobDropLogic;
import com.guayand0.mobs.utils.MobUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MobDropMixin {

    // 1.21.2 - 1.21.11
    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void onDropStacks(ServerLevel world, DamageSource damageSource, CallbackInfo ci) {

        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv) return;

        LivingEntity mob = (LivingEntity) (Object) this;
        if (!(mob.getLastAttacker() instanceof ServerPlayer player)) return;

        // Verificar si el jugador está en creativo
        if (player.getAbilities().instabuild) return;

        EntityType<?> mobType = mob.getType();
        MobCategory category = MobUtils.getCategory(mobType);

        world.getEntitiesOfClass(
                ItemEntity.class,
                mob.getBoundingBox(),
                item -> item.tickCount <= 1
        ).forEach(item -> MobDropLogic.give(player, item, category));
    }

    // 1.20.5 - 1.21.1
    /*@Inject(method = "dropLoot", at = @At("TAIL"))
    private void onDropStacks(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv) return;

        LivingEntity mob = (LivingEntity) (Object) this;
        if (!(mob.getAttacker() instanceof ServerPlayerEntity player)) return;

        // Verificar si el jugador está en creativo
        if (player.getAbilities().creativeMode) return;

        MobCategory category = MobUtils.getCategory(mob.getType());

        // Se obtiene el world desde la entidad
        if (mob.getWorld() instanceof ServerWorld world) {
            world.getEntitiesByClass(
                    ItemEntity.class,
                    mob.getBoundingBox(),
                    item -> item.age <= 1
            ).forEach(item -> MobDropLogic.give(player, item, category));
        }
    }*/
}
