package com.github.postyizhan.modernboat.item;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;
import com.github.postyizhan.modernboat.entity.EntityModernChestBoat;
import net.minecraft.world.World;

// v1.1: chest boat item. Not registered in ModItems for v1.0.
public class ItemModernChestBoat extends ItemModernBoat {
    public ItemModernChestBoat(int id) {
        super(id);
        setUnlocalizedName("modern_oak_chest_boat");
        setTextureName(Constants.MODID + ":oak_chest_boat");
    }

    @Override
    protected EntityModernBoat createBoat(World world, double x, double y, double z) {
        return new EntityModernChestBoat(world, x, y, z);
    }
}
