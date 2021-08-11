package io.tomahawkd.jflowinspector.file.protocols.ether;

// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.protocols.ipv4.Ipv4PacketImpl;


/**
 * Ethernet frame is a OSI data link layer (layer 2) protocol data unit
 * for Ethernet networks. In practice, many other networks and/or
 * in-file dumps adopted the same format for encapsulation purposes.
 *
 * @see <a href="https://ieeexplore.ieee.org/document/7428776">Source</a>
 */
public class EthernetFrame extends KaitaiStruct {

    private byte[] dstMac;
    private byte[] srcMac;
    private EtherType etherType1;
    private TagControlInfo tci;
    private EtherType etherType2;
    private Ipv4PacketImpl body;
    private EtherType etherType;

    public EthernetFrame(KaitaiStream _io) {
        super(_io);
        _read();
    }

    private void _read() {
        this.dstMac = this._io.readBytes(6);
        this.srcMac = this._io.readBytes(6);
        this.etherType1 = EtherType.byId(this._io.readU2be());
        if (etherType1() == EtherType.IEEE_802_1Q_TPID) {
            this.tci = new TagControlInfo(this._io, this);
            this.etherType2 = EtherType.byId(this._io.readU2be());
        }

        EtherType on = etherType();
        if (on != null) {
            if (etherType() == EtherType.IPV4) {
                byte[] _raw_body = this._io.readBytesFull();
                KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                this.body = new Ipv4PacketImpl(_io__raw_body);
            }
        }

    }

    /**
     * Ether type can be specied in several places in the frame. If
     * first location bears special marker (0x8100), then it is not the
     * real ether frame yet, an additional payload (`tci`) is expected
     * and real ether type is upcoming next.
     */
    public EtherType etherType() {
        if (this.etherType != null) return this.etherType;
        this.etherType = (etherType1() == EtherType.IEEE_802_1Q_TPID ? etherType2() : etherType1());
        return this.etherType;
    }

    /**
     * Destination MAC address
     */
    public byte[] dstMac() {
        return dstMac;
    }

    /**
     * Source MAC address
     */
    public byte[] srcMac() {
        return srcMac;
    }

    /**
     * Either ether type or TPID if it is a IEEE 802.1Q frame
     */
    public EtherType etherType1() {
        return etherType1;
    }

    public TagControlInfo tci() {
        return tci;
    }

    public EtherType etherType2() {
        return etherType2;
    }

    public Ipv4PacketImpl body() {
        return body;
    }
}
