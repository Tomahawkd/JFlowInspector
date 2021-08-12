package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class MSSOption extends ContentOption {

    private int maxSegSize;

    public MSSOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }

    @Override
    protected void parseContent(KaitaiStream stream) {
        this.maxSegSize = stream.readU2be();
    }

    public int getMaxSegSize() {
        return maxSegSize;
    }
}
