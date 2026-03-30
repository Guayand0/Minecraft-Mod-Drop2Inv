package com.guayand0.neoforge;

import com.guayand0.Drop2InvCommon;
import com.guayand0.blocks.CommonBlockBreakHooks;
import com.guayand0.blocks.CommonItemEntityHooks;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.logic.MobDropLogic;
import com.guayand0.mobs.utils.MobUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod(Drop2InvCommon.MOD_ID)
public final class Drop2InvNeoForge {

    public Drop2InvNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Drop2InvCommon.init(FMLPaths.CONFIGDIR.get());
        Drop2InvCommon.initClient(FMLPaths.CONFIGDIR.get());

        NeoForge.EVENT_BUS.addListener(this::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(this::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, new IConfigScreenFactory() {
            @Override
            public Screen createScreen(ModContainer container, Screen parent) {
                return new NeoForgeConfigScreen(parent);
            }
        });
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || !(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (CommonBlockBreakHooks.shouldCancelBreak(serverLevel, serverPlayer, event.getPos(), event.getState(), serverLevel.getBlockEntity(event.getPos()))) {
            event.setCanceled(true);
        }
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        if (CommonItemEntityHooks.shouldDiscard(itemEntity)) {
            event.setCanceled(true);
            itemEntity.discard();
        }
    }

    private void onLivingDrops(LivingDropsEvent event) {
        Drop2InvConfig config = Drop2InvConfigManager.get();
        if (!config.enabled || !config.mobs.mobs_to_inv) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }
        if (!(livingEntity.getLastAttacker() instanceof ServerPlayer player) || player.getAbilities().instabuild) {
            return;
        }

        MobCategory category = MobUtils.getCategory(livingEntity.getType());
        event.getDrops().removeIf(item -> MobDropLogic.give(player, item.getItem(), category));
    }

    private void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getTarget() instanceof Sheep sheep)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND && event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }

        if (player.getItemInHand(event.getHand()).is(Items.SHEARS)) {
            MobUtils.lastInteractor = player;
            Drop2InvConfig config = Drop2InvConfigManager.get();
            if (!config.enabled || !config.mobs.mobs_to_inv || !config.mobs.sheep_shear || player.getAbilities().instabuild) {
                return;
            }

            if (sheep instanceof IShearable shearable && sheep.level() instanceof ServerLevel serverLevel && shearable.isShearable(player, player.getItemInHand(event.getHand()), serverLevel, sheep.blockPosition())) {
                MobCategory category = MobUtils.getCategory(sheep.getType());
                shearable.onSheared(player, player.getItemInHand(event.getHand()), serverLevel, sheep.blockPosition())
                        .forEach(drop -> {
                            if (!MobDropLogic.give(player, drop, category)) {
                                shearable.spawnShearedDrop(serverLevel, sheep.blockPosition(), drop);
                            }
                        });
                player.getItemInHand(event.getHand()).hurtAndBreak(1, player, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
