package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class EofOption extends NoContentOption {
    public EofOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }
}
