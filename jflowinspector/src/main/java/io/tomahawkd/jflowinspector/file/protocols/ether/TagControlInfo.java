package io.tomahawkd.jflowinspector.file.protocols.ether;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;

/**
 * Tag Control Information (TCI) is an extension of IEEE 802.1Q to
 * support VLANs on normal IEEE 802.3 Ethernet network.
 */
public class TagControlInfo extends KaitaiStruct {

    private long priority;
    private boolean dropEligible;
    private long vlanId;
    private final EthernetFrame _parent;

    public TagControlInfo(KaitaiStream _io, EthernetFrame _parent) {
        super(_io);
        this._parent = _parent;
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

    public EthernetFrame _parent() {
        return _parent;
    }
}
