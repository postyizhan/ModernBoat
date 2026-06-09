package com.github.postyizhan.modernboat;

public final class Constants {
    private Constants() {}

    public static final String MODID = "modernboat";
    public static final String NAME = "Modern Boat";
    public static final String VERSION = "1.0.0";
    public static final String CHANNEL = "modernboat";

    // Item IDs (defaults; ConfigHandler may override at preInit)
    public static int ITEM_OAK_BOAT_ID = 5042;
    public static int ITEM_OAK_CHEST_BOAT_ID = 5043;

    // Mod-local entity ids
    public static final int ENTITY_BOAT_ID = 0;
    public static final int ENTITY_CHEST_BOAT_ID = 1;

    // GUI ids
    public static final int GUI_CHEST_BOAT = 0;

    // DataWatcher slots (1.6.4 safe range 16-31)
    public static final int DW_INPUT_BITS    = 20;
    public static final int DW_PADDLE_L      = 21;
    public static final int DW_PADDLE_R      = 22;
    public static final int DW_DAMAGE        = 23;
    public static final int DW_TIME_HIT      = 24;
    public static final int DW_HURT_DIR      = 25;
    public static final int DW_BUBBLE_ANGLE  = 26;
    public static final int DW_EXTRA_RIDER   = 27;
}
