package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class NoContentOption extends GenericOption {

    public NoContentOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }

    @Override
    protected void readContent() {
        // nothing to read, no content
    }
}

