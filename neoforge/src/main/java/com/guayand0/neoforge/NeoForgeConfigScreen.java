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

    private static final int MAX_SECTION_WIDTH = 310;
    private static final int ROW_HEIGHT = 20;
    private static final int MAX_TAB_WIDTH = 100;

    private final Screen parent;
    private final Drop2InvConfig workingCopy = copyConfig();
    private final List<MobEntry> mobEntries = buildMobEntries();
    private Tab activeTab = Tab.GENERAL;
    private MobsView activeMobsView = MobsView.BY_CATEGORY;
    private int mobPage;
    private int containerPage;

    public NeoForgeConfigScreen(Screen parent) {
        super(Component.translatable("drop2inv.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int sectionWidth = sectionWidth();
        int left = this.width / 2 - sectionWidth / 2;
        int tabGap = 4;
        int tabWidth = Math.min(MAX_TAB_WIDTH, Math.max(1, (this.width - 20 - tabGap * 4) / 5));
        int tabsLeft = this.width / 2 - (tabWidth * 5 + tabGap * 4) / 2;
        int footerY = this.height - 26;

        addTabButton(tabsLeft, 18, tabWidth, Tab.GENERAL, Component.translatable("drop2inv.category.general"));
        addTabButton(tabsLeft + tabWidth + tabGap, 18, tabWidth, Tab.BLOCKS, Component.translatable("drop2inv.category.blocks"));
        addTabButton(tabsLeft + (tabWidth + tabGap) * 2, 18, tabWidth, Tab.MOBS, Component.translatable("drop2inv.category.mobs"));
        addTabButton(tabsLeft + (tabWidth + tabGap) * 3, 18, tabWidth, Tab.ENTITIES, Component.translatable("drop2inv.category.entities"));
        addTabButton(tabsLeft + (tabWidth + tabGap) * 4, 18, tabWidth, Tab.CONTAINERS, Component.translatable("drop2inv.category.containers"));

        switch (activeTab) {
            case GENERAL -> buildGeneralTab(left, 48);
            case BLOCKS -> buildBlocksTab(left, 48);
            case MOBS -> buildMobsTab(left, 48, footerY);
            case ENTITIES -> buildEntitiesTab(left, 48);
            case CONTAINERS -> buildContainersTab(left, 48, footerY);
        }

        int footerButtonWidth = Math.min(100, Math.max(55, (this.width - 28) / 2));
        int footerGap = 4;
        int footerLeft = this.width / 2 - footerButtonWidth - footerGap / 2;
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(footerLeft, footerY, footerButtonWidth, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreenAndShow(parent))
                .bounds(this.width / 2 + footerGap / 2, footerY, footerButtonWidth, 20)
                .build());
    }

    private void buildGeneralTab(int left, int top) {
        addToggle(left, top, sectionWidth(), Component.translatable("drop2inv.category.general.enabled"), () -> workingCopy.enabled, value -> workingCopy.enabled = value);
    }

    private void buildBlocksTab(int left, int top) {
        List<ToggleEntry> blockSettings = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_tree_logs"), () -> workingCopy.blocks.break_tree_logs, value -> workingCopy.blocks.break_tree_logs = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_tree_leaf"), () -> workingCopy.blocks.break_tree_leaf, value -> workingCopy.blocks.break_tree_leaf = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_giant_mushroom"), () -> workingCopy.blocks.break_giant_mushroom, value -> workingCopy.blocks.break_giant_mushroom = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_vertical"), () -> workingCopy.blocks.break_vertical, value -> workingCopy.blocks.break_vertical = value),
                new ToggleEntry(Component.translatable("drop2inv.category.blocks.special.break_chorus"), () -> workingCopy.blocks.break_chorus, value -> workingCopy.blocks.break_chorus = value)
        );

        addToggle(left, top, sectionWidth(), Component.literal("Drops al inventario"), () -> workingCopy.blocks.blocks_to_inv, value -> workingCopy.blocks.blocks_to_inv = value);
        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.blocks.special"));

        int y = top + 50;
        int columnWidth = (sectionWidth() - 8) / 2;
        for (int index = 0; index < blockSettings.size(); index++) {
            ToggleEntry entry = blockSettings.get(index);
            int column = index % 2;
            int row = index / 2;
            addToggle(left + column * (columnWidth + 8), y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
        }
    }

    private void buildContainersTab(int left, int top, int footerY) {
        buildSpecialFunctions(left, top, footerY);
    }

    private void buildSpecialFunctions(int left, int top, int footerY) {
        List<ToggleEntry> containerSettings = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.chest"), () -> workingCopy.containers.chest, value -> workingCopy.containers.chest = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.trapped_chest"), () -> workingCopy.containers.trapped_chest, value -> workingCopy.containers.trapped_chest = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.copper_chests"), () -> workingCopy.containers.copper_chests, value -> workingCopy.containers.copper_chests = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.barrel"), () -> workingCopy.containers.barrel, value -> workingCopy.containers.barrel = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.dropper"), () -> workingCopy.containers.dropper, value -> workingCopy.containers.dropper = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.dispenser"), () -> workingCopy.containers.dispenser, value -> workingCopy.containers.dispenser = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.furnace"), () -> workingCopy.containers.furnace, value -> workingCopy.containers.furnace = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.smoker"), () -> workingCopy.containers.smoker, value -> workingCopy.containers.smoker = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.blast_furnace"), () -> workingCopy.containers.blast_furnace, value -> workingCopy.containers.blast_furnace = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.hopper"), () -> workingCopy.containers.hopper, value -> workingCopy.containers.hopper = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.crafter"), () -> workingCopy.containers.crafter, value -> workingCopy.containers.crafter = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.decorated_pot"), () -> workingCopy.containers.decorated_pot, value -> workingCopy.containers.decorated_pot = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.jukebox"), () -> workingCopy.containers.jukebox, value -> workingCopy.containers.jukebox = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.brewing_stand"), () -> workingCopy.containers.brewing_stand, value -> workingCopy.containers.brewing_stand = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.bookshelf"), () -> workingCopy.containers.bookshelf, value -> workingCopy.containers.bookshelf = value),
                new ToggleEntry(Component.translatable("drop2inv.category.containers.special.shelves"), () -> workingCopy.containers.shelves, value -> workingCopy.containers.shelves = value)
        );

        addToggle(left, top, sectionWidth(), Component.translatable("drop2inv.category.containers.containers_to_inv"), () -> workingCopy.containers.containers_to_inv, value -> workingCopy.containers.containers_to_inv = value);
        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.containers.special"));
        int gridY = top + 50;
        int specialPanelHeight = 68;
        int availableRows = addPagedToggles(left, gridY, footerY, containerSettings, containerPage, page -> containerPage = page, specialPanelHeight + 24);
        int containerLastPage = maxPage(containerSettings.size(), availableRows * 2);
        int specialTop = gridY + availableRows * (ROW_HEIGHT + 2) + (containerLastPage > 0 ? 24 : 0) + 4;
        addSectionTitle(left, specialTop, Component.translatable("drop2inv.category.containers.special_entities"));
        int specialY = specialTop + 22;
        int gap = 8;
        int columnWidth = (sectionWidth() - gap) / 2;
        addToggle(left, specialY, columnWidth, Component.translatable("drop2inv.category.entities.special.special_minecarts"), () -> workingCopy.entities.special_minecarts, value -> workingCopy.entities.special_minecarts = value);
        addToggle(left + columnWidth + gap, specialY, columnWidth, Component.translatable("drop2inv.category.entities.special.item_frames"), () -> workingCopy.entities.item_frames, value -> workingCopy.entities.item_frames = value);
        addToggle(left, specialY + ROW_HEIGHT + 2, columnWidth, Component.translatable("drop2inv.category.entities.special.armor_stand"), () -> workingCopy.entities.armor_stand, value -> workingCopy.entities.armor_stand = value);
        addToggle(left + columnWidth + gap, specialY + ROW_HEIGHT + 2, columnWidth, Component.translatable("drop2inv.category.containers.special.chest_boats"), () -> workingCopy.entities.chest_boats, value -> workingCopy.entities.chest_boats = value);
    }

    private void buildEntitiesTab(int left, int top) {
        List<ToggleEntry> entitySettings = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.boats"), () -> workingCopy.entities.boats, value -> workingCopy.entities.boats = value),
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.chest_boats"), () -> workingCopy.entities.chest_boats, value -> workingCopy.entities.chest_boats = value),
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.minecarts"), () -> workingCopy.entities.minecarts, value -> workingCopy.entities.minecarts = value),
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.special_minecarts"), () -> workingCopy.entities.special_minecarts, value -> workingCopy.entities.special_minecarts = value),
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.armor_stand"), () -> workingCopy.entities.armor_stand, value -> workingCopy.entities.armor_stand = value),
                new ToggleEntry(Component.translatable("drop2inv.category.entities.special.item_frames"), () -> workingCopy.entities.item_frames, value -> workingCopy.entities.item_frames = value)
        );

        addToggle(left, top, sectionWidth(), Component.translatable("drop2inv.category.entities.entities_to_inv"), () -> workingCopy.entities.entities_to_inv, value -> workingCopy.entities.entities_to_inv = value);
        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.entities.special"));

        int y = top + 50;
        int columnGap = 8;
        int columnWidth = (sectionWidth() - columnGap) / 2;
        for (int index = 0; index < entitySettings.size(); index++) {
            ToggleEntry entry = entitySettings.get(index);
            int column = index % 2;
            int row = index / 2;
            addToggle(left + column * (columnWidth + columnGap), y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
        }
    }

    private void buildMobsTab(int left, int top, int footerY) {
        List<ToggleEntry> mobCategories = List.of(
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.hostile"), () -> workingCopy.mobs.hostile, value -> workingCopy.mobs.hostile = value),
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.neutral"), () -> workingCopy.mobs.neutral, value -> workingCopy.mobs.neutral = value),
                new ToggleEntry(Component.translatable("drop2inv.category.mobs.category.passive"), () -> workingCopy.mobs.passive, value -> workingCopy.mobs.passive = value)
        );

        addToggle(left, top, sectionWidth(), Component.literal("Drops al inventario"), () -> workingCopy.mobs.mobs_to_inv, value -> workingCopy.mobs.mobs_to_inv = value);

        addSectionTitle(left, top + 28, Component.translatable("drop2inv.category.mobs.special"));
        addToggle(left, top + 50, sectionWidth(), Component.translatable("drop2inv.category.mobs.special.sheep_shear"), () -> workingCopy.mobs.sheep_shear, value -> workingCopy.mobs.sheep_shear = value);

        int subTabsTop = top + 84;
        int subtabGap = 8;
        int subtabWidth = (sectionWidth() - subtabGap) / 2;
        addMobsSubTabButton(left, subTabsTop, subtabWidth, MobsView.BY_CATEGORY, Component.translatable("drop2inv.category.mobs.category"));
        addMobsSubTabButton(left + subtabWidth + subtabGap, subTabsTop, subtabWidth, MobsView.BY_MOB, Component.translatable("drop2inv.category.mobs.per_mob_category"));

        if (activeMobsView == MobsView.BY_CATEGORY) {
            int y = subTabsTop + 28;
            int columnWidth = (sectionWidth() - 8) / 2;
            for (int index = 0; index < mobCategories.size(); index++) {
                ToggleEntry entry = mobCategories.get(index);
                int column = index % 2;
                int row = index / 2;
                addToggle(left + column * (columnWidth + 8), y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
            }
        } else {
            int mobSectionTop = subTabsTop + 28;
            int availableRows = Math.max(1, Math.min(3, (footerY - mobSectionTop - 26) / (ROW_HEIGHT + 2)));
            int itemsPerPage = availableRows * 2;
            mobPage = Math.min(mobPage, maxPage(mobEntries.size(), itemsPerPage));
            int y = mobSectionTop;
            int columnWidth = (sectionWidth() - 8) / 2;
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
                        .bounds(left + column * (columnWidth + 8), y + row * (ROW_HEIGHT + 2), columnWidth, ROW_HEIGHT)
                        .build());
            }

            int lastPage = maxPage(mobEntries.size(), itemsPerPage);
            if (lastPage > 0) {
                addPager(left, y + availableRows * (ROW_HEIGHT + 2) + 4, mobPage, lastPage, page -> mobPage = page);
            }
        }
    }

    private void addTabButton(int x, int y, int width, Tab tab, Component label) {
        Button button = this.addRenderableWidget(Button.builder(label, value -> {
                    activeTab = tab;
                    this.init();
                })
                .bounds(x, y, width, 20)
                .build());
        button.active = activeTab != tab;
    }

    private void addMobsSubTabButton(int x, int y, int width, MobsView view, Component label) {
        Button button = this.addRenderableWidget(Button.builder(label, value -> {
                    activeMobsView = view;
                    this.init();
                })
                .bounds(x, y, width, 20)
                .build());
        button.active = activeMobsView != view;
    }

    private int addPagedToggles(int left, int y, int footerY, List<ToggleEntry> entries, int page, PageSetter pageSetter, int reservedHeight) {
        int rowsPerPage = Math.max(1, Math.min(3, (footerY - y - reservedHeight) / (ROW_HEIGHT + 2)));
        int itemsPerPage = rowsPerPage * 2;
        int lastPage = maxPage(entries.size(), itemsPerPage);
        page = Math.min(page, lastPage);
        pageSetter.set(page);

        int columnGap = 8;
        int columnWidth = (sectionWidth() - columnGap) / 2;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, entries.size());
        for (int index = start; index < end; index++) {
            ToggleEntry entry = entries.get(index);
            int relative = index - start;
            int column = relative % 2;
            int row = relative / 2;
            addToggle(left + column * (columnWidth + columnGap), y + row * (ROW_HEIGHT + 2), columnWidth, entry.label(), entry.getter(), entry.setter());
        }

        if (lastPage > 0) {
            addPager(left, y + rowsPerPage * (ROW_HEIGHT + 2) + 4, page, lastPage, pageSetter);
        }
        return rowsPerPage;
    }

    private void addSectionTitle(int x, int y, Component title) {
        Button sectionButton = this.addRenderableWidget(Button.builder(title, button -> {
                })
                .bounds(x, y, sectionWidth(), 16)
                .build());
        sectionButton.active = false;
    }

    private void addPager(int x, int y, int currentPage, int maxPage, PageSetter pageSetter) {
        int pagerWidth = sectionWidth();
        int arrowWidth = Math.min(40, Math.max(24, (pagerWidth - 16) / 5));
        int gap = 4;
        int centerWidth = pagerWidth - arrowWidth * 2 - gap * 2;
        Button previousButton = this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                    pageSetter.set(Math.max(0, currentPage - 1));
                    this.init();
                })
                .bounds(x, y, arrowWidth, 20)
                .build());
        previousButton.active = currentPage > 0;

        Button pageButton = this.addRenderableWidget(Button.builder(Component.literal("Page " + (currentPage + 1) + "/" + (maxPage + 1)), button -> {
                })
                .bounds(x + arrowWidth + gap, y, centerWidth, 20)
                .build());
        pageButton.active = false;

        Button nextButton = this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                    pageSetter.set(Math.min(maxPage, currentPage + 1));
                    this.init();
                })
                .bounds(x + pagerWidth - arrowWidth, y, arrowWidth, 20)
                .build());
        nextButton.active = currentPage < maxPage;
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
        target.entities.entities_to_inv = workingCopy.entities.entities_to_inv;
        target.entities.boats = workingCopy.entities.boats;
        target.entities.chest_boats = workingCopy.entities.chest_boats;
        target.entities.minecarts = workingCopy.entities.minecarts;
        target.entities.special_minecarts = workingCopy.entities.special_minecarts;
        target.entities.armor_stand = workingCopy.entities.armor_stand;
        target.entities.item_frames = workingCopy.entities.item_frames;
        target.containers.containers_to_inv = workingCopy.containers.containers_to_inv;
        target.containers.chest = workingCopy.containers.chest;
        target.containers.trapped_chest = workingCopy.containers.trapped_chest;
        target.containers.copper_chests = workingCopy.containers.copper_chests;
        target.containers.barrel = workingCopy.containers.barrel;
        target.containers.dropper = workingCopy.containers.dropper;
        target.containers.dispenser = workingCopy.containers.dispenser;
        target.containers.furnace = workingCopy.containers.furnace;
        target.containers.smoker = workingCopy.containers.smoker;
        target.containers.blast_furnace = workingCopy.containers.blast_furnace;
        target.containers.hopper = workingCopy.containers.hopper;
        target.containers.crafter = workingCopy.containers.crafter;
        target.containers.decorated_pot = workingCopy.containers.decorated_pot;
        target.containers.jukebox = workingCopy.containers.jukebox;
        target.containers.brewing_stand = workingCopy.containers.brewing_stand;
        target.containers.bookshelf = workingCopy.containers.bookshelf;
        target.containers.shelves = workingCopy.containers.shelves;
        target.mobs.mobs_to_inv = workingCopy.mobs.mobs_to_inv;
        target.mobs.hostile = workingCopy.mobs.hostile;
        target.mobs.neutral = workingCopy.mobs.neutral;
        target.mobs.passive = workingCopy.mobs.passive;
        target.mobs.sheep_shear = workingCopy.mobs.sheep_shear;
        target.mobs.individual_category.clear();
        target.mobs.individual_category.putAll(workingCopy.mobs.individual_category);

        try {
            Drop2InvConfigManager.save();
            this.minecraft.setScreenAndShow(parent);
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
        copy.entities.entities_to_inv = source.entities.entities_to_inv;
        copy.entities.boats = source.entities.boats;
        copy.entities.chest_boats = source.entities.chest_boats;
        copy.entities.minecarts = source.entities.minecarts;
        copy.entities.special_minecarts = source.entities.special_minecarts;
        copy.entities.armor_stand = source.entities.armor_stand;
        copy.entities.item_frames = source.entities.item_frames;
        copy.containers.containers_to_inv = source.containers.containers_to_inv;
        copy.containers.chest = source.containers.chest;
        copy.containers.trapped_chest = source.containers.trapped_chest;
        copy.containers.copper_chests = source.containers.copper_chests;
        copy.containers.barrel = source.containers.barrel;
        copy.containers.dropper = source.containers.dropper;
        copy.containers.dispenser = source.containers.dispenser;
        copy.containers.furnace = source.containers.furnace;
        copy.containers.smoker = source.containers.smoker;
        copy.containers.blast_furnace = source.containers.blast_furnace;
        copy.containers.hopper = source.containers.hopper;
        copy.containers.crafter = source.containers.crafter;
        copy.containers.decorated_pot = source.containers.decorated_pot;
        copy.containers.jukebox = source.containers.jukebox;
        copy.containers.brewing_stand = source.containers.brewing_stand;
        copy.containers.bookshelf = source.containers.bookshelf;
        copy.containers.shelves = source.containers.shelves;
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

    private int sectionWidth() {
        return Math.max(1, Math.min(MAX_SECTION_WIDTH, this.width - 20));
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
        MOBS,
        ENTITIES,
        CONTAINERS
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
