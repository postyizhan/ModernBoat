package com.github.postyizhan.modernboat.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

// v1.1: 27 chest slots + 36 player slots; canInteractWith ties to chest boat lifecycle.
public class ContainerChestBoat extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return false;
    }
}
