package io.tomahawkd.jflowinspector.file.pcapng;

import io.kaitai.struct.KaitaiStream;
import io.tomahawkd.jflowinspector.file.protocols.ipv4.Ipv4Packet;
import io.tomahawkd.jflowinspector.file.protocols.tcp.TcpSegment;
import io.tomahawkd.jflowinspector.file.protocols.ether.EthernetFrame;
import io.tomahawkd.jflowinspector.file.PcapPacket;

public abstract class AbstractPacketBlock extends GenericBlock implements PcapPacket {

    protected long inclLen;
    protected long oriLen;

    protected EthernetFrame body;

    public AbstractPacketBlock(EndianDeclaredKaitaiStream _io, Pcapng parent, BlockType type) {
        super(_io, parent, type);
    }

    public abstract void readBody(KaitaiStream stream);

    @Override
    public Ipv4Packet ip() {
        if (body == null) return null;
        return body.body();
    }

    @Override
    public TcpSegment tcp() {
        if (body == null || body.body() == null) return null;
        return body.body().body();
    }

    public abstract long getInterfaceId();

    public InterfaceDescription getInterface() {
        try {
            return this.parent().descs().get((int) getInterfaceId());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
