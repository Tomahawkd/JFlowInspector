package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

public interface TcpOption {

    int type();

    TcpOptionType parsedType();

    int length();

    byte[] rawData();
}
