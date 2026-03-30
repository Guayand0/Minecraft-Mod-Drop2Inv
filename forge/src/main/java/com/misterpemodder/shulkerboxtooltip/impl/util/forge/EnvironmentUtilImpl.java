package com.misterpemodder.shulkerboxtooltip.impl.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class EnvironmentUtilImpl {

  private EnvironmentUtilImpl() {
  }

  /**
   * Implementation of {@link com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil#isClient()}.
   */
  public static boolean isClient() {
    return FMLEnvironment.dist == Dist.CLIENT;
  }
}
