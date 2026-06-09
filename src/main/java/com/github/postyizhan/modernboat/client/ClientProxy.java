package com.github.postyizhan.modernboat.client;

import com.github.postyizhan.modernboat.CommonProxy;
import com.github.postyizhan.modernboat.client.render.RenderModernBoat;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers() {
        RenderModernBoat renderer = new RenderModernBoat();
        RenderingRegistry.registerEntityRenderingHandler(EntityModernBoat.class, renderer);
        TickRegistry.registerTickHandler(new KeyInputHandler(), Side.CLIENT);
    }
}
