package com.github.postyizhan.modernboat.network;

import com.github.postyizhan.modernboat.Constants;
import com.github.postyizhan.modernboat.entity.EntityModernBoat;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

// Single packet handler for the "modernboat" channel.
// Routes the one-byte paddle-input payload from PacketPaddleInput.pack() back
// onto the server-side boat the player is riding.
public class ModernBoatPacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player playerObj) {
        if (payload == null || payload.data == null || payload.data.length < 1) {
            return;
        }
        if (!Constants.CHANNEL.equals(payload.channel)) {
            return;
        }
        if (!(playerObj instanceof EntityPlayerMP)) {
            return; // server-side only path
        }

        EntityPlayer player = (EntityPlayer) playerObj;
        if (!(player.ridingEntity instanceof EntityModernBoat)) {
            return;
        }
        EntityModernBoat boat = (EntityModernBoat) player.ridingEntity;
        if (boat.riddenByEntity != player) {
            return; // only the primary driver may steer
        }

        PacketPaddleInput input = PacketPaddleInput.unpack(payload.data[0]);
        boat.setInput(input.forward, input.back, input.left, input.right);
    }
}
