package com.guayand0.config;

import com.guayand0.mobs.MobCategory;

import java.util.HashMap;
import java.util.Map;

public class Drop2InvConfig {

    public static final Drop2InvConfig DEFAULTS = new Drop2InvConfig();

    public boolean enabled = true;

    public Blocks blocks = new Blocks();
    public Mobs mobs = new Mobs();
    public Entities entities = new Entities();
    public Containers containers = new Containers();

    public static class Blocks {
        public boolean blocks_to_inv = true;

        public boolean break_tree_logs = true;
        public boolean break_tree_leaf = true;
        public boolean break_giant_mushroom = false; // Experimental

        //public boolean break_crops = true;
        public boolean break_vertical = true;
        public boolean break_chorus = true;
    }

    public static class Mobs {
        public boolean mobs_to_inv = true;

        public boolean hostile = true;
        public boolean neutral = true;
        public boolean passive = true;

        public boolean sheep_shear = true;

        // mob_id -> category
        public Map<String, MobCategory> individual_category = new HashMap<>();
    }

    public static class Entities {
        public boolean entities_to_inv = true;

        public boolean boats = true;
        public boolean chest_boats = true;
        public boolean minecarts = true;
        public boolean special_minecarts = true;
        public boolean armor_stand = true;
        public boolean item_frames = true;
    }

    public static class Containers {
        public boolean containers_to_inv = true;

        public boolean chest = true;
        public boolean trapped_chest = true;
        public boolean copper_chests = true;
        public boolean barrel = true;
        public boolean dropper = true;
        public boolean dispenser = true;
        public boolean furnace = true;
        public boolean smoker = true;
        public boolean blast_furnace = true;
        public boolean hopper = true;
        public boolean crafter = true;
        // Temporarily disabled; the old implementation is preserved in SpecialContainerBlockMixin.java.disabled.
        // public boolean lectern = false;
        public boolean decorated_pot = true;
        public boolean jukebox = true;
        public boolean brewing_stand = true;
        // public boolean campfires = false;
        public boolean bookshelf = true;
        public boolean shelves = true;
    }
}
