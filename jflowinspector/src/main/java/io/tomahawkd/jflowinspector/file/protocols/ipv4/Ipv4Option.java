package io.tomahawkd.jflowinspector.file.protocols.ipv4;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;

public class Ipv4Option extends KaitaiStruct {

    private Integer copy;
    private Integer optClass;
    private Integer number;
    private int b1;
    private int len;
    private byte[] body;
    private final Ipv4OptionList _parent;

    public Ipv4Option(KaitaiStream _io, Ipv4OptionList _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        this.b1 = this._io.readU1();
        this.len = this._io.readU1();
        this.body = this._io.readBytes((len() > 2 ? (len() - 2) : 0));
    }

    public Integer copy() {
        if (this.copy != null)
            return this.copy;
        this.copy = ((b1() & 128) >> 7);
        return this.copy;
    }

    public Integer optClass() {
        if (this.optClass != null)
            return this.optClass;
        this.optClass = ((b1() & 96) >> 5);
        return this.optClass;
    }

    public Integer number() {
        if (this.number != null)
            return this.number;
        this.number = (b1() & 31);
        return this.number;
    }

    public int b1() {
        return b1;
    }

    public int len() {
        return len;
    }

    public byte[] body() {
        return body;
    }

    public Ipv4OptionList _parent() {
        return _parent;
    }
}
