package com.github.postyizhan.modernboat;

import java.io.File;
import net.minecraftforge.common.Configuration;

public final class ConfigHandler {
    private ConfigHandler() {}

    public static void load(File configFile) {
        Configuration cfg = new Configuration(configFile);
        try {
            cfg.load();
            Constants.ITEM_OAK_BOAT_ID = cfg.get(
                "items", "oakBoat", Constants.ITEM_OAK_BOAT_ID,
                "Item ID for modern oak boat. Change here if it clashes with another mod."
            ).getInt();
            Constants.ITEM_OAK_CHEST_BOAT_ID = cfg.get(
                "items", "oakChestBoat", Constants.ITEM_OAK_CHEST_BOAT_ID,
                "Item ID reserved for v1.1 oak chest boat."
            ).getInt();
        } finally {
            if (cfg.hasChanged()) cfg.save();
        }
    }
}
