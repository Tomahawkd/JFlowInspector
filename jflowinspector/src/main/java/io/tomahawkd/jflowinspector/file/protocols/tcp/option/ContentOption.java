package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;

public class ContentOption extends GenericOption {

    private int length;

    private byte[] content;

    public ContentOption(KaitaiStream _io, TcpOptionType type) {
        super(_io, type);
    }

    public ContentOption(KaitaiStream _io, int type) {
        super(_io, type);
    }

    @Override
    protected void readContent() {
        this.length = _io.readU1();
        // content length = total length - option_kind(1) - length(1)
        this.content = _io.readBytes(length - 2);
        parseContent(new ByteBufferKaitaiStream(this.content));
    }

    protected void parseContent(KaitaiStream stream) {

    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public byte[] rawData() {
        return content;
    }
}
