package io.tomahawkd.jflowinspector.file.jnetpcap;

import io.tomahawkd.jflowinspector.file.protocols.ipv4.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.protocols.tcp.TcpSegment;
import io.tomahawkd.jflowinspector.file.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class PcapPacketAdapter implements PcapPacket {

    private final org.jnetpcap.packet.PcapPacket packet;


    public PcapPacketAdapter(org.jnetpcap.packet.PcapPacket packet) {
        this.packet = packet;
        packet.scan(Ethernet.ID);
    }

    @Override
    public Ipv4Packet ip() {
        Ip4 ip4 = new Ip4();
        if (packet.hasHeader(ip4)) return new Ipv4PacketAdapter(ip4);
        return null;
    }

    @Override
    public TcpSegment tcp() {
        Tcp tcp = new Tcp();
        if (packet.hasHeader(tcp)) return new TcpSegmentAdapter(tcp);
        return null;
    }

    @Override
    public long getTimestamp() {
        return packet.getCaptureHeader().timestampInMicros();
    }
}
