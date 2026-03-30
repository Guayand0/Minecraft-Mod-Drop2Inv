package com.guayand0.neoforge;

import com.guayand0.Drop2InvCommon;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import com.guayand0.mobs.MobCategory;
import com.guayand0.mobs.config.MobConfigManager;
import com.guayand0.mobs.utils.MobUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class NeoForgeConfigScreen extends Screen {

    private static final int SECTION_WIDTH = 310;
    private static final int ROW_HEIGHT = 20;
    private static final int TAB_WIDTH = 100;
    private static final int SUBTAB_WIDTH = 150;
    private static final int MOBS_PER_PAGE = 5;

    private final Screen parent;
    private final Drop2InvConfig workingCopy = copyConfig();
    private final List<MobEntry> mobEntries = buildMobEntries();
    private Tab activeTab = Tab.GENERAL;
    private MobsView activeMobsView = MobsView.BY_CATEGORY;
    private int mobPage;

    public NeoForgeConfigScreen(Screen parent) {
        super(Component.translatable("drop2inv.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int left = this.width / 2 - SECTION_WIDTH / 2;
        int tabsLeft = this.width / 2 - (TAB_WIDTH * 3 + 10) / 2;
        int footerY = this.height - 26;

        addTabButton(tabsLeft, 18, Tab.GENERAL, Component.translatable("drop2inv.category.general"));
        addTabButton(tabsLeft + TAB_WIDTH + 5, 18, Tab.BLOCKS, Component.translatable("drop2inv.category.blocks"));
        addTabButton(tabsLeft + (TAB_WIDTH + 5) * 2, 18, Tab.MOBS, Component.translatable("drop2inv.category.mobs"));

        switch (activeTab) {
            case GENERAL -> buildGeneralTab(left, 48);
            case BLOCKS -> buildBlocksTab(left, 48);
            case MOBS -> buildMobsTab(left, 48, footerY);
        }

        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(this.width / 2 - 102, footerY, 100, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 + 2, footerY, 100, 20)
                .build());
    }

    private void buildGeneralTab(int left, int top) {
        addToggle(left, top, SECTION_WIDTH, Component.translatable("drop2inv.category.general.enabled"), () -> workingCopy.enabled, value -> workingCopy.enabled = value);
    }

    private void buildBlocksTab(int left, int top) {
        List<ToggleEntry> blockSettings = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_tree_logs"), () -> workingCopy.blocks.break_tree_logs, value -> workingCopy.blocks.break_tree_logs = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_tree_leaf"), () -> workingCopy.blocks.break_tree_leaf, value -> workingCopy.blocks.break_tree_leaf = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_giant_mushroom"), () -> workingCopy.blocks.break_giant_mushroom, value -> workingCopy.blocks.break_giant_mushroom = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_vertical"), () -> workingCopy.blocks.break_vertical, value -> workingCopy.blocks.break_vertical = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_chorus"), () -> workingCopy.blocks.break_chorus, value -> workingCopy.blocks.break_chorus = value)
        );

        addToggle(left, top, SECTION_WIDTH, Component.literal("Drops al inventario"), () -> workingCopy.blocks.blocks_to_inv, value -> workingCopy.blocks.blocks_to_inv = value);
        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.blocks.special"));

        int y = top + 50;
        int columnWidth = 150;
        for (int index = 0; index < blockSettings.size(); index++) {
            ToggleEntry entry = blockSettings.get(index);
            int column = index % 2;
            int row = index / 2;
            addToggle(left + column * 160, y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
        }
    }

    private void buildMobsTab(int left, int top, int footerY) {
        List<ToggleEntry> mobCategories = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.hostile"), () -> workingCopy.mobs.hostile, value -> workingCopy.mobs.hostile = value),
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.neutral"), () -> workingCopy.mobs.neutral, value -> workingCopy.mobs.neutral = value),
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.passive"), () -> workingCopy.mobs.passive, value -> workingCopy.mobs.passive = value)
        );

        addToggle(left, top, SECTION_WIDTH, Component.literal("Drops al inventario"), () -> workingCopy.mobs.mobs_to_inv, value -> workingCopy.mobs.mobs_to_inv = value);

        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.mobs.special"));
        addToggle(left, top + 50, SECTION_WIDTH, Component.translatable("drop2inv.category.mobs.special.sheep_shear"), () -> workingCopy.mobs.sheep_shear, value -> workingCopy.mobs.sheep_shear = value);

        int subTabsTop = top + 84;
        addMobsSubTabButton(left, subTabsTop, MobsView.BY_CATEGORY, Component.translatable("drop2inv.category.mobs.category"));
        addMobsSubTabButton(left + SUBTAB_WIDTH + 10, subTabsTop, MobsView.BY_MOB, Component.translatable("drop2inv.category.mobs.per_mob_category"));

        if (activeMobsView == MobsView.BY_CATEGORY) {
            int y = subTabsTop + 28;
            int columnWidth = 150;
            for (int index = 0; index < mobCategories.size(); index++) {
                ToggleEntry entry = mobCategories.get(index);
                int column = index % 2;
                int row = index / 2;
                addToggle(left + column * 160, y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
            }
        } else {
            int mobSectionTop = subTabsTop + 28;
            int availableRows = Math.max(2, Math.min(3, (footerY - mobSectionTop - 46) / ROW_HEIGHT));
            int itemsPerPage = availableRows * 2;
            mobPage = Math.min(mobPage, maxPage(mobEntries.size(), itemsPerPage));
            int y = mobSectionTop;
            int columnWidth = 150;
            int pageStart = mobPage * itemsPerPage;
            int pageEnd = Math.min(pageStart + itemsPerPage, mobEntries.size());
            for (int index = pageStart; index < pageEnd; index++) {
                MobEntry mobEntry = mobEntries.get(index);
                int relative = index - pageStart;
                int column = relative % 2;
                int row = relative / 2;
                this.addRenderableWidget(Button.builder(mobLabel(mobEntry), button -> {
                            MobCategory next = nextCategory(workingCopy.mobs.individual_category.getOrDefault(mobEntry.mobId(), mobEntry.defaultCategory()));
                            workingCopy.mobs.individual_category.put(mobEntry.mobId(), next);
                            button.setMessage(mobLabel(mobEntry));
                        })
                        .bounds(left + column * 160, y + row * (ROW_HEIGHT + 2), columnWidth, ROW_HEIGHT)
                        .build());
            }

            addPager(left, y + availableRows * (ROW_HEIGHT + 2) + 4, mobPage, maxPage(mobEntries.size(), itemsPerPage), page -> mobPage = page);
        }
    }

    private void addTabButton(int x, int y, Tab tab, Component label) {
        Button button = this.addRenderableWidget(Button.builder(label, value -> {
                    activeTab = tab;
                    this.init();
                })
                .bounds(x, y, TAB_WIDTH, 20)
                .build());
        button.active = activeTab != tab;
    }

    private void addMobsSubTabButton(int x, int y, MobsView view, Component label) {
        Button button = this.addRenderableWidget(Button.builder(label, value -> {
                    activeMobsView = view;
                    this.init();
                })
                .bounds(x, y, SUBTAB_WIDTH, 20)
                .build());
        button.active = activeMobsView != view;
    }

    private void addSectionTitle(int x, int y, Component title) {
        Button sectionButton = this.addRenderableWidget(Button.builder(title, button -> {
                })
                .bounds(x, y, SECTION_WIDTH, 16)
                .build());
        sectionButton.active = false;
    }

    private void addPager(int x, int y, int currentPage, int maxPage, PageSetter pageSetter) {
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                    pageSetter.set(Math.max(0, currentPage - 1));
                    this.init();
                })
                .bounds(x, y, 40, 20)
                .build());

        Button pageButton = this.addRenderableWidget(Button.builder(Component.literal("Page " + (currentPage + 1) + "/" + (maxPage + 1)), button -> {
                })
                .bounds(x + 50, y, 210, 20)
                .build());
        pageButton.active = false;

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                    pageSetter.set(Math.min(maxPage, currentPage + 1));
                    this.init();
                })
                .bounds(x + 270, y, 40, 20)
                .build());
    }

    private void addToggle(int x, int y, int width, Component text, ToggleGetter getter, ToggleSetter setter) {
        this.addRenderableWidget(Button.builder(label(text, getter.get()), button -> {
                    setter.set(!getter.get());
                    button.setMessage(label(text, getter.get()));
                })
                .bounds(x, y, width, ROW_HEIGHT)
                .build());
    }

    private void saveAndClose() {
        Drop2InvConfig target = Drop2InvConfigManager.get();
        target.enabled = workingCopy.enabled;
        target.blocks.blocks_to_inv = workingCopy.blocks.blocks_to_inv;
        target.blocks.break_tree_logs = workingCopy.blocks.break_tree_logs;
        target.blocks.break_tree_leaf = workingCopy.blocks.break_tree_leaf;
        target.blocks.break_giant_mushroom = workingCopy.blocks.break_giant_mushroom;
        target.blocks.break_vertical = workingCopy.blocks.break_vertical;
        target.blocks.break_chorus = workingCopy.blocks.break_chorus;
        target.mobs.mobs_to_inv = workingCopy.mobs.mobs_to_inv;
        target.mobs.hostile = workingCopy.mobs.hostile;
        target.mobs.neutral = workingCopy.mobs.neutral;
        target.mobs.passive = workingCopy.mobs.passive;
        target.mobs.sheep_shear = workingCopy.mobs.sheep_shear;
        target.mobs.individual_category.clear();
        target.mobs.individual_category.putAll(workingCopy.mobs.individual_category);

        try {
            Drop2InvConfigManager.save();
            this.minecraft.setScreen(parent);
        } catch (IOException exception) {
            Drop2InvCommon.LOGGER.error("Failed to save Drop2Inv config", exception);
        }
    }

    private static Drop2InvConfig copyConfig() {
        Drop2InvConfig source = Drop2InvConfigManager.get();
        Drop2InvConfig copy = new Drop2InvConfig();
        copy.enabled = source.enabled;
        copy.blocks.blocks_to_inv = source.blocks.blocks_to_inv;
        copy.blocks.break_tree_logs = source.blocks.break_tree_logs;
        copy.blocks.break_tree_leaf = source.blocks.break_tree_leaf;
        copy.blocks.break_giant_mushroom = source.blocks.break_giant_mushroom;
        copy.blocks.break_vertical = source.blocks.break_vertical;
        copy.blocks.break_chorus = source.blocks.break_chorus;
        copy.mobs.mobs_to_inv = source.mobs.mobs_to_inv;
        copy.mobs.hostile = source.mobs.hostile;
        copy.mobs.neutral = source.mobs.neutral;
        copy.mobs.passive = source.mobs.passive;
        copy.mobs.sheep_shear = source.mobs.sheep_shear;
        copy.mobs.individual_category.putAll(source.mobs.individual_category);
        return copy;
    }

    private static Component label(Component text, boolean enabled) {
        return text.copy().append(Component.literal(": " + (enabled ? "ON" : "OFF")));
    }

    private Component mobLabel(MobEntry mobEntry) {
        MobCategory category = workingCopy.mobs.individual_category.getOrDefault(mobEntry.mobId(), mobEntry.defaultCategory());
        return Component.literal(mobEntry.displayName() + ": " + category.name());
    }

    private static int maxPage(int size, int itemsPerPage) {
        if (size == 0) {
            return 0;
        }
        return (size - 1) / itemsPerPage;
    }

    private static MobCategory nextCategory(MobCategory current) {
        return switch (current) {
            case HOSTILE -> MobCategory.NEUTRAL;
            case NEUTRAL -> MobCategory.PASSIVE;
            case PASSIVE -> MobCategory.HOSTILE;
        };
    }

    private static List<MobEntry> buildMobEntries() {
        MobConfigManager mobConfig = MobConfigManager.get();
        Set<String> allMobs = new HashSet<>();
        allMobs.addAll(mobConfig.getPassive());
        allMobs.addAll(mobConfig.getNeutral());
        allMobs.addAll(mobConfig.getHostile());

        return allMobs.stream()
                .map(mobId -> {
                    Identifier id = Identifier.tryParse(mobId);
                    if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                        return null;
                    }

                    String key = "entity." + id.getNamespace() + "." + id.getPath();
                    String name = Component.translatable(key).getString();
                    MobCategory defaultCategory = MobUtils.getDefaultCategory(mobId, mobConfig);
                    return new MobEntry(mobId, name, defaultCategory);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MobEntry::displayName))
                .toList();
    }

    private enum Tab {
        GENERAL,
        BLOCKS,
        MOBS
    }

    private enum MobsView {
        BY_CATEGORY,
        BY_MOB
    }

    private record ToggleEntry(Component label, ToggleGetter getter, ToggleSetter setter) {
    }

    private record MobEntry(String mobId, String displayName, MobCategory defaultCategory) {
    }

    private interface ToggleGetter {
        boolean get();
    }

    private interface ToggleSetter {
        void set(boolean value);
    }

    private interface PageSetter {
        void set(int value);
    }
}
