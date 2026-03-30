package com.guayand0.mixin;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.logic.MobDropLogic;
import com.guayand0.mobs.utils.MobUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sheep.class)
public abstract class SheepShearMixin {

    // 1.21.2 - 1.21.11
    @Inject(method = "shear", at = @At("TAIL"))
    private void onSheared(ServerLevel world, SoundSource soundCategory, ItemStack shears, CallbackInfo ci) {

        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv || !config.mobs.sheep_shear) return;

        if (!(MobUtils.lastInteractor instanceof ServerPlayer player)) return;

        // Verificar si el jugador está en creativo
        if (player.getAbilities().instabuild) return;

        Sheep sheep = (Sheep) (Object) this;
        MobCategory category = MobUtils.getCategory(sheep.getType());

        world.getEntitiesOfClass(
                ItemEntity.class,
                sheep.getBoundingBox(),
                item -> item.tickCount <= 1
        ).forEach(item -> MobDropLogic.give(player, item, category));
    }

    // 1.20.5 - 1.21.1
    /*@Inject(method = "sheared", at = @At("TAIL"))
    private void onSheared(SoundCategory shearedSoundCategory, CallbackInfo ci) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv || !config.mobs.sheep_shear) return;

        if (!(MobUtils.lastInteractor instanceof ServerPlayerEntity player)) return;

        // Verificar si el jugador está en creativo
        if (player.getAbilities().creativeMode) return;

        SheepEntity sheep = (SheepEntity) (Object) this;
        MobCategory category = MobUtils.getCategory(sheep.getType());

        // Se obtiene el world desde la entidad
        if (sheep.getWorld() instanceof ServerWorld world) {
            world.getEntitiesByClass(
                    ItemEntity.class,
                    sheep.getBoundingBox(),
                    item -> item.age <= 1
            ).forEach(item -> MobDropLogic.give(player, item, category));
        }
    }*/
}
