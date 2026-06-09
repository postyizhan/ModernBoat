package com.github.postyizhan.modernboat.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public final class ModRecipes {
    private ModRecipes() {}

    public static void init() {
        // Oak boat: U-shape, 5 oak planks (planks meta=0 is oak in 1.6.4).
        GameRegistry.addRecipe(
            new ItemStack(ModItems.itemOakBoat),
            "P P", "PPP",
            'P', new ItemStack(Block.planks, 1, 0)
        );
    }
}
