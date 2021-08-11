package io.tomahawkd.jflowinspector.pcap.parse;

public interface PcapFileReader {

    boolean hasNext();

    /**
     * @return packet. If the packet is null, it indicates the EOF.
     */
    PcapPacket next();
}
