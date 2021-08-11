package io.tomahawkd.jflowinspector.file.protocols.ether;

// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.protocols.ip.Ipv4PacketImpl;


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

    private final EthernetFrame _root;
    private final KaitaiStruct _parent;

    public EthernetFrame(KaitaiStream _io) {
        this(_io, null, null);
    }

    public EthernetFrame(KaitaiStream _io, KaitaiStruct _parent, EthernetFrame _root) {
        super(_io);
        this._parent = _parent;
        this._root = _root == null ? this : _root;
        _read();
    }

    private void _read() {
        this.dstMac = this._io.readBytes(6);
        this.srcMac = this._io.readBytes(6);
        this.etherType1 = EtherType.byId(this._io.readU2be());
        if (etherType1() == EtherType.IEEE_802_1Q_TPID) {
            this.tci = new TagControlInfo(this._io, this, _root);
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

    public EthernetFrame _root() {
        return _root;
    }

    public KaitaiStruct _parent() {
        return _parent;
    }

    /**
     * Tag Control Information (TCI) is an extension of IEEE 802.1Q to
     * support VLANs on normal IEEE 802.3 Ethernet network.
     */
    public static class TagControlInfo extends KaitaiStruct {

        private long priority;
        private boolean dropEligible;
        private long vlanId;
        private final EthernetFrame _root;
        private final EthernetFrame _parent;

        public TagControlInfo(KaitaiStream _io, EthernetFrame _parent, EthernetFrame _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }

        private void _read() {
            this.priority = this._io.readBitsIntBe(3);
            this.dropEligible = this._io.readBitsIntBe(1) != 0;
            this.vlanId = this._io.readBitsIntBe(12);
        }

        /**
         * Priority Code Point (PCP) is used to specify priority for
         * different kinds of traffic.
         */
        public long priority() {
            return priority;
        }

        /**
         * Drop Eligible Indicator (DEI) specifies if frame is eligible
         * to dropping while congestion is detected for certain classes
         * of traffic.
         */
        public boolean dropEligible() {
            return dropEligible;
        }

        /**
         * VLAN Identifier (VID) specifies which VLAN this frame
         * belongs to.
         */
        public long vlanId() {
            return vlanId;
        }

        public EthernetFrame _root() {
            return _root;
        }

        public EthernetFrame _parent() {
            return _parent;
        }
    }
}
