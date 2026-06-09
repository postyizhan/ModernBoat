package com.github.postyizhan.modernboat.registry;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.item.ItemModernBoat;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public final class ModItems {
    private ModItems() {}

    public static Item itemOakBoat;

    public static void init() {
        // 1.6.4 Item ctor takes id - 256 internally.
        itemOakBoat = new ItemModernBoat(Constants.ITEM_OAK_BOAT_ID - 256);
        GameRegistry.registerItem(itemOakBoat, "modern_oak_boat");
    }
}
