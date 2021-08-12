package io.tomahawkd.jflowinspector.file.protocols.tcp;

// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.protocols.tcp.option.WindowScaleOption;


/**
 * TCP is one of the core Internet protocols on transport layer (AKA
 * OSI layer 4), providing stateful connections with error checking,
 * guarantees of delivery, order of segments and avoidance of duplicate
 * delivery.
 */
public class TcpSegmentImpl extends KaitaiStruct implements TcpSegment {

    private int srcPort;
    private int dstPort;
    private long seqNum;
    private long ackNum;
    private int offset;
    private int flags;
    private int windowSize;
    private int checksum;
    private int urgentPointer;
    private TcpOptionList options;
    private int windowScaler = 1;
    private byte[] body;
    private final KaitaiStruct _parent;

    public TcpSegmentImpl(KaitaiStream _io) {
        this(_io, null);
    }

    public TcpSegmentImpl(KaitaiStream _io, KaitaiStruct _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        this.srcPort = this._io.readU2be();
        this.dstPort = this._io.readU2be();
        this.seqNum = this._io.readU4be();
        this.ackNum = this._io.readU4be();

        // copied from https://datatracker.ietf.org/doc/html/rfc3168#section-23.2
        //      0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
        //   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
        //   |               |               | C | E | U | A | P | R | S | F |
        //   | Header Length |    Reserved   | W | C | R | C | S | S | Y | I |
        //   |               |               | R | E | G | K | H | T | N | N |
        //   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
        //
        int b12 = this._io.readU1();
        this.offset = (b12 & 0b11110000) >> 4;
        this.flags = this._io.readU1();
        this.windowSize = this._io.readU2be();
        this.checksum = this._io.readU2be();
        this.urgentPointer = this._io.readU2be();
        // the first 5 lines (32 bits/4 bytes per line) are mandatory
        if (offset > 5) {
            byte[] optionsAndPaddings = this._io.readBytes((offset - 5) * 4L);
            this.options = new TcpOptionList(new ByteBufferKaitaiStream(optionsAndPaddings));
            WindowScaleOption wso = this.options.getOptionByType(WindowScaleOption.class);
            if (wso != null) this.windowScaler = wso.scale();
        }
        this.body = this._io.readBytesFull();
    }

    @Override
    public int srcPort() {
        return srcPort;
    }

    @Override
    public int dstPort() {
        return dstPort;
    }

    @Override
    public long seq() {
        return seqNum;
    }

    @Override
    public long ack() {
        return ackNum;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int headerLength() {
        return offset * 4;
    }

    @Override
    public int flags() {
        return flags;
    }

    @Override
    public boolean getFlag(int mask) {
        return (flags & mask) != 0;
    }

    @Override
    public boolean flag_cwr() {
        return getFlag(FLAG_CWR);
    }

    @Override
    public boolean flag_ece() {
        return getFlag(FLAG_ECE);
    }

    @Override
    public boolean flag_urg() {
        return getFlag(FLAG_URG);
    }

    @Override
    public boolean flag_ack() {
        return getFlag(FLAG_ACK);
    }

    @Override
    public boolean flag_psh() {
        return getFlag(FLAG_PSH);
    }

    @Override
    public boolean flag_rst() {
        return getFlag(FLAG_RST);
    }

    @Override
    public boolean flag_syn() {
        return getFlag(FLAG_SYN);
    }

    @Override
    public boolean flag_fin() {
        return getFlag(FLAG_FIN);
    }

    @Override
    public int window() {
        return windowSize;
    }

    @Override
    public int windowScaler() {
        return windowScaler;
    }

    @Override
    public int checksum() {
        return checksum;
    }

    @Override
    public int urgentPointer() {
        return urgentPointer;
    }

    public TcpOptionList options() {
        return options;
    }

    @Override
    public byte[] payload() {
        return body;
    }

    @Override
    public int payloadLength() {
        return body.length;
    }

    public KaitaiStruct _parent() {
        return _parent;
    }
}
