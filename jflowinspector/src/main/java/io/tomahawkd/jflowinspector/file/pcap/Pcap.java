package io.tomahawkd.jflowinspector.file.pcap;

// This is a generated file but has some modifications.

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.BigFileKaitaiStream;
import io.tomahawkd.jflowinspector.file.LinkType;
import io.tomahawkd.jflowinspector.file.PcapFileReader;
import io.tomahawkd.jflowinspector.file.PcapPacket;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * PCAP (named after libpcap / winpcap) is a popular format for saving
 * network traffic grabbed by network sniffers. It is typically
 * produced by tools like [tcpdump](https://www.tcpdump.org/) or
 * [Wireshark](https://www.wireshark.org/).
 *
 * @see <a href="http://wiki.wireshark.org/Development/LibpcapFileFormat">Source</a>
 */
public class Pcap extends KaitaiStruct implements PcapFileReader {

    private final Header hdr;

    public static Pcap fromFile(Path file) throws IOException {
        return new Pcap(new BigFileKaitaiStream(file));
    }

    public Pcap(KaitaiStream _io) {
        super(_io);
        this.hdr = new Header(this._io, this);
    }

    public boolean hasNext() {
        return !this._io.isEof();
    }

    public PcapPacket next() {
        return hasNext() ? new Packet(this._io, this) : null;
    }

    // for compatibility leave empty here
    @SuppressWarnings("unused")
    private void _read() {
    }

    public Header hdr() {
        return hdr;
    }

    public KaitaiStruct _parent() {
        return null;
    }
}
