package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;

public abstract class GenericOption extends KaitaiStruct implements TcpOption {

    private final int type;
    private final TcpOptionType parsed;

    public GenericOption(KaitaiStream _io, TcpOptionType type) {
        super(_io);
        this.type = type.type();
        this.parsed = type;
        readContent();
    }

    public GenericOption(KaitaiStream _io, int type) {
        super(_io);
        this.type = type;
        parsed = TcpOptionType.getById(type);
        readContent();
    }

    protected abstract void readContent();

    @Override
    public int type() {
        return this.type;
    }

    @Override
    public TcpOptionType parsedType() {
        return parsed;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public byte[] rawData() {
        return new byte[0];
    }
}
