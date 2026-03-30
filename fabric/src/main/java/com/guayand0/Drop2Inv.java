package com.guayand0;

import com.guayand0.fabric.FabricDrop2InvBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Drop2Inv implements ModInitializer {

	public static final String MOD_ID = "drop2inv";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Drop2InvCommon.init(FabricLoader.getInstance().getConfigDir());
		FabricDrop2InvBootstrap.register();
		LOGGER.info("Drop2Inv initialized for Fabric");
	}
}
