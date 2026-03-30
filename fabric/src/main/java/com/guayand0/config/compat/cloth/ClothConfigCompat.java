package com.guayand0.config.compat.cloth;

import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.config.MobConfigManager;
import com.guayand0.mobs.utils.MobUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClothConfigCompat {

    private static final String CONFIG_VALUE = "drop2inv.category.";

    private record MobEntry(String mobId, String displayName) {}

    public static Screen create(Screen parent) {
        Drop2InvClothConfig clothConfig = new Drop2InvClothConfig();
        Drop2InvConfig config = Drop2InvConfigManager.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Drop2Inv Config"))
                .setSavingRunnable(clothConfig::saveToManager);

        ConfigEntryBuilder entry = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable(CONFIG_VALUE + "general"));
        general.addEntry(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "general.enabled"), config.enabled)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "general.enabled.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.enabled)
                        .setSaveConsumer(v -> {
                            config.enabled = v;
                            clothConfig.enabled = v;
                        }).build()
        );

        ConfigCategory blocks = builder.getOrCreateCategory(Component.translatable(CONFIG_VALUE + "blocks"));
        blocks.addEntry(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.blocks_to_inv"), config.blocks.blocks_to_inv)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.blocks_to_inv.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.blocks_to_inv)
                        .setSaveConsumer(v -> config.blocks.blocks_to_inv = v).build()
        );

        SubCategoryBuilder blocksSpecial = entry.startSubCategory(Component.translatable(CONFIG_VALUE + "blocks.special"));
        blocksSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.special.break_tree_logs"), config.blocks.break_tree_logs)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.special.break_tree_logs.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.break_tree_logs)
                        .setSaveConsumer(v -> config.blocks.break_tree_logs = v).build()
        );
        blocksSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.special.break_tree_leaf"), config.blocks.break_tree_leaf)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.special.break_tree_leaf.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.break_tree_leaf)
                        .setSaveConsumer(v -> config.blocks.break_tree_leaf = v).build()
        );
        blocksSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.special.break_giant_mushroom"), config.blocks.break_giant_mushroom)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.special.break_giant_mushroom.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.break_giant_mushroom)
                        .setSaveConsumer(v -> config.blocks.break_giant_mushroom = v).build()
        );
        blocksSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.special.break_vertical"), config.blocks.break_vertical)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.special.break_vertical.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.break_vertical)
                        .setSaveConsumer(v -> config.blocks.break_vertical = v).build()
        );
        blocksSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "blocks.special.break_chorus"), config.blocks.break_chorus)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "blocks.special.break_chorus.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.blocks.break_chorus)
                        .setSaveConsumer(v -> config.blocks.break_chorus = v).build()
        );
        blocks.addEntry(blocksSpecial.build());

        ConfigCategory mobs = builder.getOrCreateCategory(Component.translatable(CONFIG_VALUE + "mobs"));
        mobs.addEntry(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "mobs.mobs_to_inv"), config.mobs.mobs_to_inv)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "mobs.mobs_to_inv.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.mobs.mobs_to_inv)
                        .setSaveConsumer(v -> config.mobs.mobs_to_inv = v).build()
        );

        SubCategoryBuilder mobsSpecial = entry.startSubCategory(Component.translatable(CONFIG_VALUE + "mobs.special"));
        mobsSpecial.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "mobs.special.sheep_shear"), config.mobs.sheep_shear)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "mobs.special.sheep_shear.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.mobs.sheep_shear)
                        .setSaveConsumer(v -> config.mobs.sheep_shear = v).build()
        );
        mobs.addEntry(mobsSpecial.build());

        SubCategoryBuilder mobsCategory = entry.startSubCategory(Component.translatable(CONFIG_VALUE + "mobs.category"));
        mobsCategory.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "mobs.category.hostile"), config.mobs.hostile)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "mobs.category.hostile.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.mobs.hostile)
                        .setSaveConsumer(v -> config.mobs.hostile = v).build()
        );
        mobsCategory.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "mobs.category.neutral"), config.mobs.neutral)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "mobs.category.neutral.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.mobs.neutral)
                        .setSaveConsumer(v -> config.mobs.neutral = v).build()
        );
        mobsCategory.add(
                entry.startBooleanToggle(Component.translatable(CONFIG_VALUE + "mobs.category.passive"), config.mobs.passive)
                        .setTooltip(Component.translatable(CONFIG_VALUE + "mobs.category.passive.@Tooltip"))
                        .setDefaultValue(Drop2InvConfig.DEFAULTS.mobs.passive)
                        .setSaveConsumer(v -> config.mobs.passive = v).build()
        );
        mobs.addEntry(mobsCategory.build());

        SubCategoryBuilder perMobCategory = entry.startSubCategory(Component.translatable(CONFIG_VALUE + "mobs.per_mob_category"));

        MobConfigManager mobConfig = MobConfigManager.get();
        Set<String> allMobs = new HashSet<>();
        allMobs.addAll(mobConfig.getPassive());
        allMobs.addAll(mobConfig.getNeutral());
        allMobs.addAll(mobConfig.getHostile());

        List<MobEntry> mobsSorted = allMobs.stream()
                .map(mobId -> {
                    Identifier id = Identifier.tryParse(mobId);
                    if (id == null) return null;
                    if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return null;

                    String key = "entity." + id.getNamespace() + "." + id.getPath();
                    String name = Component.translatable(key).getString().replace("_", " ").toUpperCase();
                    return new MobEntry(mobId, name);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MobEntry::displayName))
                .toList();

        for (MobEntry entryMob : mobsSorted) {
            String mobId = entryMob.mobId();
            String displayName = entryMob.displayName();
            Identifier id = Identifier.tryParse(mobId);

            MobCategory defaultCategory = MobUtils.getDefaultCategory(mobId, mobConfig);
            MobCategory currentCategory = config.mobs.individual_category.getOrDefault(mobId, defaultCategory);

            String tooltipMobId = id == null ? mobId : id.getPath().toUpperCase();
            String tooltipText = Component.translatable(CONFIG_VALUE + "mobs.per_mob_category.individual.@Tooltip", tooltipMobId).getString();

            perMobCategory.add(
                    entry.startEnumSelector(Component.literal(displayName), MobCategory.class, currentCategory)
                            .setTooltip(Component.literal(tooltipText))
                            .setDefaultValue(defaultCategory)
                            .setSaveConsumer(v -> config.mobs.individual_category.put(mobId, v)).build()
            );
        }
        mobs.addEntry(perMobCategory.build());

        return builder.build();
    }
}
