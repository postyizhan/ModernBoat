package com.github.postyizhan.modernboat.network;

// Serialized over a 1.6.4 Packet250CustomPayload to channel "modernboat".
// Wire format: one byte with bits {0:forward, 1:back, 2:left, 3:right}.
public class PacketPaddleInput {
    public boolean forward;
    public boolean back;
    public boolean left;
    public boolean right;

    public PacketPaddleInput() {}

    public PacketPaddleInput(boolean forward, boolean back, boolean left, boolean right) {
        this.forward = forward;
        this.back = back;
        this.left = left;
        this.right = right;
    }

    public byte pack() {
        return (byte) ((forward ? 1 : 0) | (back ? 2 : 0) | (left ? 4 : 0) | (right ? 8 : 0));
    }

    public static PacketPaddleInput unpack(byte bits) {
        return new PacketPaddleInput(
            (bits & 1) != 0,
            (bits & 2) != 0,
            (bits & 4) != 0,
            (bits & 8) != 0
        );
    }
}
