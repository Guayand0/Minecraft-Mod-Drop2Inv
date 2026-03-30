package com.guayand0.config.compat.modmenu;

import com.guayand0.Drop2InvClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Drop2InvClient::getConfigScreen;
    }
}
