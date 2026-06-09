package com.github.postyizhan.modernboat.registry;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.ModernBoat;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import cpw.mods.fml.common.registry.EntityRegistry;

public final class ModEntities {
    private ModEntities() {}

    public static void init() {
        EntityRegistry.registerModEntity(
            EntityModernBoat.class,
            "modern_boat",
            Constants.ENTITY_BOAT_ID,
            ModernBoat.instance,
            80, 3, true
        );
    }
}
