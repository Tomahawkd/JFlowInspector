package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class UnknownOption extends ContentOption {
    public UnknownOption(KaitaiStream _io, int type) {
        super(_io, type);
    }
}
