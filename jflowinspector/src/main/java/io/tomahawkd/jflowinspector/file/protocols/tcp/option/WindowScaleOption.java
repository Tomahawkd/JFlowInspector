package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;

public class WindowScaleOption extends ContentOption {

    private int scale;

    public WindowScaleOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }

    @Override
    protected void parseContent(KaitaiStream stream) {
        this.scale = stream.readU1();
    }

    public int scale() {
        return scale;
    }
}
