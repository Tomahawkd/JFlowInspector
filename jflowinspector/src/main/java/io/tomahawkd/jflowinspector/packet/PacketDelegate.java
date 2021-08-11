package io.tomahawkd.jflowinspector.packet;


import io.tomahawkd.jflowinspector.pcap.parse.PcapPacket;

public interface PacketDelegate {

    boolean parse(PacketInfo dst, PcapPacket packet);
}
