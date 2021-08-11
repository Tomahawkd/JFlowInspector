package io.tomahawkd.jflowinspector.file.protocols.ipv4;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.data.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.protocols.tcp.TcpSegmentImpl;

public class Ipv4PacketImpl extends KaitaiStruct implements Ipv4Packet {

    private int version;
    private int ihl;
    private int ihlBytes;
    private int serviceType;
    private int totalLength;
    private int identification;
    private int b67;
    private int ttl;
    private int protocol;
    private int headerChecksum;
    private byte[] srcIpAddr;
    private byte[] dstIpAddr;
    private Ipv4OptionList options;
    private TcpSegmentImpl body;

    private final KaitaiStruct _parent;

    public Ipv4PacketImpl(KaitaiStream _io) {
        this(_io, null);
    }

    public Ipv4PacketImpl(KaitaiStream _io, KaitaiStruct _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        int b1 = this._io.readU1();
        this.version = (b1 & 0b11110000) >> 4;
        this.ihl = b1 & 0b00001111;
        this.ihlBytes = ihl * 4;
        this.serviceType = this._io.readU1();
        this.totalLength = this._io.readU2be();
        this.identification = this._io.readU2be();
        this.b67 = this._io.readU2be();
        this.ttl = this._io.readU1();
        this.protocol = this._io.readU1();
        this.headerChecksum = this._io.readU2be();
        this.srcIpAddr = this._io.readBytes(4);
        this.dstIpAddr = this._io.readBytes(4);
        byte[] _raw_options = this._io.readBytes((ihlBytes - 20));
        this.options = new Ipv4OptionList(new ByteBufferKaitaiStream(_raw_options), this);
        byte[] _raw_body = this._io.readBytes((totalLength - ihlBytes));
        Protocol protocol = Protocol.byId(protocol());
        if (protocol == Protocol.TCP) {
            this.body = new TcpSegmentImpl(new ByteBufferKaitaiStream(_raw_body));
        }
    }

    @Override
    public int version() {
        return this.version;
    }

    @Override
    public int ihl() {
        return this.ihl;
    }

    @Override
    public int ihlBytes() {
        return this.ihlBytes;
    }

    @Override
    public int serviceType() {
        return serviceType;
    }

    @Override
    public int totalLength() {
        return totalLength;
    }

    @Override
    public int identification() {
        return identification;
    }

    public int b67() {
        return b67;
    }

    @Override
    public int ttl() {
        return ttl;
    }

    @Override
    public int protocol() {
        return protocol;
    }

    @Override
    public int headerChecksum() {
        return headerChecksum;
    }

    @Override
    public byte[] source() {
        return srcIpAddr;
    }

    @Override
    public byte[] destination() {
        return dstIpAddr;
    }

    public Ipv4OptionList options() {
        return options;
    }

    public TcpSegmentImpl body() {
        return body;
    }

    public KaitaiStruct _parent() {
        return _parent;
    }
}
