package io.tomahawkd.jflowinspector.file.protocols.ipv4;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;

import java.util.ArrayList;
import java.util.List;

public class Ipv4OptionList extends KaitaiStruct {

    private List<Ipv4Option> entries;
    private final Ipv4PacketImpl _parent;

    public Ipv4OptionList(KaitaiStream _io, Ipv4PacketImpl _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        this.entries = new ArrayList<>();
        while (!this._io.isEof()) {
            this.entries.add(new Ipv4Option(this._io, this));
        }
    }

    public List<Ipv4Option> entries() {
        return entries;
    }

    public Ipv4PacketImpl _parent() {
        return _parent;
    }
}
