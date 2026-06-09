package com.github.postyizhan.modernboat.client;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class KeyInputHandler implements ITickHandler {
    private int lastSent = -1;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.getNetHandler() == null) {
            this.lastSent = -1;
            return;
        }
        if (!(mc.thePlayer.ridingEntity instanceof EntityModernBoat)) {
            if (this.lastSent > 0) {
                this.sendInput(mc, 0);
            }
            this.lastSent = -1;
            return;
        }

        int bits = 0;
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) {
            bits |= 1;
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack)) {
            bits |= 2;
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) {
            bits |= 4;
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) {
            bits |= 8;
        }

        ((EntityModernBoat) mc.thePlayer.ridingEntity).setInput(
            (bits & 1) != 0,
            (bits & 2) != 0,
            (bits & 4) != 0,
            (bits & 8) != 0
        );

        if (bits != this.lastSent) {
            this.sendInput(mc, bits);
            this.lastSent = bits;
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() {
        return "ModernBoatKeyInput";
    }

    private void sendInput(Minecraft mc, int bits) {
        mc.getNetHandler().addToSendQueue(new Packet250CustomPayload(Constants.CHANNEL, new byte[] {(byte) bits}));
    }
}
