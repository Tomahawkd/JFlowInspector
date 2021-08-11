package io.tomahawkd.jflowinspector.pcap.parse;

import io.tomahawkd.jflowinspector.pcap.data.Ipv4Packet;
import io.tomahawkd.jflowinspector.pcap.data.TcpSegment;

public interface PcapPacket {

    Ipv4Packet ip();

    TcpSegment tcp();

    long getTimestamp();
}
