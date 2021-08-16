package io.tomahawkd.jflowinspector.file.protocols.ipv4;

public interface Ipv4Packet {

    int version();

    int ihl();

    int ihlBytes();

    int serviceType();

    int totalLength();

    int identification();

    int ttl();

    int protocol();

    int headerChecksum();

    byte[] source();

    byte[] destination();
}
