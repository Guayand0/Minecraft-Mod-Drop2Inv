package com.guayand0.config;

import com.guayand0.mobs.MobCategory;

import java.util.HashMap;
import java.util.Map;

public class Drop2InvConfig {

    public static final Drop2InvConfig DEFAULTS = new Drop2InvConfig();

    public boolean enabled = true;

    public Blocks blocks = new Blocks();
    public Mobs mobs = new Mobs();

    public static class Blocks {
        public boolean blocks_to_inv = true;

        public boolean break_tree_logs = true;
        public boolean break_tree_leaf = true;
        public boolean break_giant_mushroom = false;

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
}
