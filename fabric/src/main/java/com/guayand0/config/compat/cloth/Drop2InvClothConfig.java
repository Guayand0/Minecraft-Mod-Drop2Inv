package com.guayand0.config.compat.cloth;

import com.guayand0.blocks.DropTracker;
import com.guayand0.config.Drop2InvConfig;
import com.guayand0.config.Drop2InvConfigManager;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "drop2inv_cloth")
public class Drop2InvClothConfig implements ConfigData {

    public boolean enabled;

    public Drop2InvClothConfig() {
        Drop2InvConfig cfg = Drop2InvConfigManager.get();
        this.enabled = cfg.enabled;
    }

    public void saveToManager() {
        Drop2InvConfig cfg = Drop2InvConfigManager.get();
        cfg.enabled = this.enabled;
        try {
            Drop2InvConfigManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DropTracker.clear();
    }
}
