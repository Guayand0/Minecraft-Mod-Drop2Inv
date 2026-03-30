package com.guayand0.forge;

import com.guayand0.Drop2InvCommon;
import com.guayand0.blocks.CommonBlockBreakHooks;
import com.guayand0.blocks.CommonItemEntityHooks;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.logic.MobDropLogic;
import com.guayand0.mobs.utils.MobUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

@Mod(Drop2InvCommon.MOD_ID)
public final class Drop2InvForge {

    public Drop2InvForge() {
        Drop2InvCommon.init(java.nio.file.Path.of("config"));
        Drop2InvCommon.initClient(java.nio.file.Path.of("config"));

        BlockEvent.BreakEvent.BUS.addListener((Predicate<BlockEvent.BreakEvent>) this::onBlockBreak);
        EntityJoinLevelEvent.BUS.addListener((Predicate<EntityJoinLevelEvent>) this::onEntityJoinLevel);
        LivingDropsEvent.BUS.addListener((Predicate<LivingDropsEvent>) this::onLivingDrops);
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener((Predicate<PlayerInteractEvent.EntityInteractSpecific>) this::onEntityInteract);

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new ForgeConfigScreen(parent))
        );
    }

    private boolean onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || !(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (CommonBlockBreakHooks.shouldCancelBreak(serverLevel, serverPlayer, event.getPos(), event.getState(), serverLevel.getBlockEntity(event.getPos()))) {
            event.setResult(Result.DENY);
            return true;
        }
        return false;
    }

    private boolean onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return false;
        }

        if (CommonItemEntityHooks.shouldDiscard(itemEntity)) {
            itemEntity.discard();
            return true;
        }
        return false;
    }

    private boolean onLivingDrops(LivingDropsEvent event) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv) {
            return false;
        }
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return false;
        }
        if (!(livingEntity.getLastAttacker() instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return false;
        }

        MobCategory category = MobUtils.getCategory(livingEntity.getType());
        event.getDrops().removeIf(item -> MobDropLogic.give(player, item.getItem(), category));
        return false;
    }

    private boolean onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getTarget() instanceof Sheep sheep)) {
            return false;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return false;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND && event.getHand() != InteractionHand.OFF_HAND) {
            return false;
        }

        if (player.getItemInHand(event.getHand()).is(Items.SHEARS)) {
            MobUtils.lastInteractor = player;
            Drop2InvConfig config = Drop2InvConfigManager.get();
            if (!config.enabled || !config.mobs.mobs_to_inv || !config.mobs.sheep_shear || player.getAbilities().instabuild) {
                return false;
            }

            if (sheep instanceof IForgeShearable shearable && sheep.level() instanceof ServerLevel serverLevel && shearable.isShearable(player.getItemInHand(event.getHand()), serverLevel, sheep.blockPosition())) {
                MobCategory category = MobUtils.getCategory(sheep.getType());
                shearable.onSheared(player, player.getItemInHand(event.getHand()), serverLevel, sheep.blockPosition(), 0)
                        .forEach(drop -> {
                            if (!MobDropLogic.give(player, drop, category)) {
                                sheep.spawnAtLocation(serverLevel, drop, 1.0F);
                            }
                        });
                player.getItemInHand(event.getHand()).hurtAndBreak(1, player, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return true;
            }
        }
        return false;
    }
}
