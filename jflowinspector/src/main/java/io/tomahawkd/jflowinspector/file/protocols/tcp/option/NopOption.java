package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class NopOption extends NoContentOption {
    public NopOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }
}
