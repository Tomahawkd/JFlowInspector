package io.tomahawkd.jflowinspector.file.pcap;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.data.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.data.TcpSegment;
import io.tomahawkd.jflowinspector.file.protocols.ether.EthernetFrame;
import io.tomahawkd.jflowinspector.file.LinkType;
import io.tomahawkd.jflowinspector.file.PcapPacket;

/**
 * @see <a href="https://wiki.wireshark.org/Development/LibpcapFileFormat#Record_.28Packet.29_Header">Source</a>
 */
public class Packet extends KaitaiStruct implements PcapPacket {

    /**
     * The date and time when this packet was captured. This value is in seconds since January 1, 1970 00:00:00 GMT.
     */
    private long tsSec;

    /**
     * the microseconds when this packet was captured, as an offset to ts_sec.
     */
    private long tsUsec;

    /**
     * the number of bytes of packet data actually captured and saved in the file.
     * This value should never become larger than orig_len or the snaplen value of the global header.
     */
    private long inclLen;

    /**
     * the length in bytes of the packet as it appeared on the network when it was captured.
     * If incl_len and orig_len differ, the actually saved packet size was limited by snaplen.
     */
    private long origLen;

    /**
     * Parsed Packet body/content
     */
    private EthernetFrame body = null;

    private final Pcap _parent;

    public Packet(KaitaiStream _io, Pcap _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        this.tsSec = this._io.readU4le();
        this.tsUsec = this._io.readU4le();
        this.inclLen = this._io.readU4le();
        this.origLen = this._io.readU4le();
        LinkType on = _parent.hdr().network();
        if (on != null) {
            if (_parent.hdr().network() == LinkType.ETHERNET) {
                byte[] _raw_body = this._io.readBytes(inclLen());
                KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                this.body = new EthernetFrame(_io__raw_body);
            }
        }
    }

    public EthernetFrame ethernet() {
        return this.body;
    }

    @Override
    public Ipv4Packet ip() {
        if (body == null) return null;
        return body.body();
    }

    @Override
    public TcpSegment tcp() {
        if (body == null || body.body() == null) return null;
        return body.body().body();
    }

    /**
     * @see Packet#tsSec
     */
    public long tsSec() {
        return tsSec;
    }

    /**
     * @see Packet#tsUsec
     */
    public long tsUsec() {
        return tsUsec;
    }

    public long getTimestamp() {
        return tsSec * 1_000_000L + tsUsec;
    }

    /**
     * Number of bytes of packet data actually captured and saved in the file.
     */
    public long inclLen() {
        return inclLen;
    }

    /**
     * Length of the packet as it appeared on the network when it was captured.
     */
    public long origLen() {
        return origLen;
    }

    /**
     * @see <a href="https://wiki.wireshark.org/Development/LibpcapFileFormat#Packet_Data">Source</a>
     */
    public Object body() {
        return body;
    }

    public Pcap _parent() {
        return _parent;
    }
}