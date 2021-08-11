package io.tomahawkd.jflowinspector.packet;


import io.tomahawkd.jflowinspector.file.PcapPacket;

public interface PacketDelegate {

    boolean parse(PacketInfo dst, PcapPacket packet);
}
