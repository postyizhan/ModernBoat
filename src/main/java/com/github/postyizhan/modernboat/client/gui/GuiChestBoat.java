package com.github.postyizhan.modernboat.client.gui;

import com.github.postyizhan.modernboat.inventory.ContainerChestBoat;
import net.minecraft.client.gui.inventory.GuiContainer;

// v1.1: 27-slot chest boat GUI.
public class GuiChestBoat extends GuiContainer {
    public GuiChestBoat() {
        super(new ContainerChestBoat());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // v1.1
    }
}
