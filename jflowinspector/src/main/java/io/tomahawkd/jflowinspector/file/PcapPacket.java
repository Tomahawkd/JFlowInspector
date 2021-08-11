package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.jflowinspector.file.data.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.data.TcpSegment;

public interface PcapPacket {

    Ipv4Packet ip();

    TcpSegment tcp();

    long getTimestamp();
}
