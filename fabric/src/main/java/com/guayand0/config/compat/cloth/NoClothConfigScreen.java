package com.guayand0.config.compat.cloth;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NoClothConfigScreen extends Screen {

    private final Screen parent;
    private static final String CONFIG_VALUE = "drop2inv.no_cloth_config.";

    public NoClothConfigScreen(Screen parent) {
        super(Component.literal("Drop2Inv Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;
        int buttonY = this.height / 2 + 20;

        // Texto 1
        String msg1 = Component.translatable(CONFIG_VALUE + "L1").getString();
        int w1 = this.font.width(msg1);

        EditBox text1 = new EditBox(this.font, centerX - w1 / 2 - 4, buttonY - 50, w1 + 10, 18, Component.empty());
        text1.setMaxLength(msg1.length());
        text1.setValue(msg1);
        text1.setEditable(false);
        text1.setBordered(false);
        this.addRenderableWidget(text1);

        // Texto 2
        String msg2 = Component.translatable(CONFIG_VALUE + "L2").getString();
        int w2 = this.font.width(msg2);

        EditBox text2 = new EditBox(this.font, centerX - w2 / 2 - 4, buttonY - 30, w2 + 10, 18, Component.empty());
        text2.setMaxLength(msg2.length());
        text2.setValue(msg2);
        text2.setEditable(false);
        text2.setBordered(false);
        this.addRenderableWidget(text2);

        // Botón volver
        this.addRenderableWidget(Button.builder(Component.literal("Volver"), button ->
                        this.minecraft.setScreen(parent)).bounds(centerX - 50, buttonY, 100, 20).build()
        );
    }

}
