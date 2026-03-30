package com.guayand0;

import com.guayand0.config.compat.cloth.ClothConfigCompat;
import com.guayand0.config.compat.cloth.Drop2InvClothConfig;
import com.guayand0.config.compat.cloth.NoClothConfigScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

import static com.guayand0.Drop2Inv.MOD_ID;

public class Drop2InvClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Drop2InvCommon.initClient(FabricLoader.getInstance().getConfigDir());

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(Drop2InvClothConfig.class, GsonConfigSerializer::new);
        }
    }

    public static Screen getConfigScreen(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfigCompat.create(parent);
        }

        return new NoClothConfigScreen(parent);
    }
}
