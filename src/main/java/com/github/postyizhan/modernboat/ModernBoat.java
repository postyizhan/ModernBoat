package com.github.postyizhan.modernboat;

import com.github.postyizhan.modernboat.handler.GuiHandler;
import com.github.postyizhan.modernboat.network.ModernBoatNetwork;
import com.github.postyizhan.modernboat.registry.ModEntities;
import com.github.postyizhan.modernboat.registry.ModItems;
import com.github.postyizhan.modernboat.registry.ModRecipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION)
@NetworkMod(channels = {Constants.CHANNEL}, packetHandler = com.github.postyizhan.modernboat.network.ModernBoatPacketHandler.class, clientSideRequired = true, serverSideRequired = false)
public class ModernBoat {

    @Mod.Instance(Constants.MODID)
    public static ModernBoat instance;

    @SidedProxy(
        clientSide = "com.github.postyizhan.modernboat.client.ClientProxy",
        serverSide = "com.github.postyizhan.modernboat.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.load(event.getSuggestedConfigurationFile());
        ModItems.init();
        ModEntities.init();
        ModernBoatNetwork.init();
        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.init();
        proxy.registerRenderers();
    }
}
