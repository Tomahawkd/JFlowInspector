package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.jflowinspector.file.protocols.ipv4.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.protocols.tcp.TcpSegment;

public interface PcapPacket {

    Ipv4Packet ip();

    TcpSegment tcp();

    long getTimestamp();
}
