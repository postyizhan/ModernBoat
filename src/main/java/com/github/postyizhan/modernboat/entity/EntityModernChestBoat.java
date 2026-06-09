package com.github.postyizhan.modernboat.entity;

import net.minecraft.world.World;

// v1.1: implements IInventory (27 slots) + opens GUI on shift-right-click.
public class EntityModernChestBoat extends EntityModernBoat {
    public EntityModernChestBoat(World world) {
        super(world);
    }

    public EntityModernChestBoat(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
}
