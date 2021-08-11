package io.tomahawkd.jflowinspector.packet;

import io.tomahawkd.jflowinspector.file.data.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.PcapPacket;

@Layer(LayerType.INTERNET)
public class Ipv4PacketDelegate implements PacketDelegate {

    public boolean parse(PacketInfo dst, PcapPacket packet) {

        Ipv4Packet ip4 = packet.ip();
        if (ip4 == null) return false;

        dst.addFeature(MetaFeature.SRC, ip4.source());
        dst.addFeature(MetaFeature.DST, ip4.destination());
        dst.addFeature(MetaFeature.IPV4, true);
        return true;
    }
}
