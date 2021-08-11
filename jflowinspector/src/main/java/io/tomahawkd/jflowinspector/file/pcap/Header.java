package io.tomahawkd.jflowinspector.file.pcap;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.LinkType;

import java.util.Arrays;

/**
 * @see <a href="https://wiki.wireshark.org/Development/LibpcapFileFormat#Global_Header">Source</a>
 */
public class Header extends KaitaiStruct {

    private byte[] magicNumber;

    /**
     * Major version, currently 2.
     */
    private int versionMajor;

    /**
     * Minor version, currently 4.
     */
    private int versionMinor;

    /**
     * the correction time in seconds between GMT (UTC) and the local timezone of
     * the following packet header timestamps. In practice, time stamps are always in GMT, so thiszone is always 0.
     */
    private int thiszone;

    /**
     * in theory, the accuracy of time stamps in the capture; in practice, all tools set it to 0.
     */
    private long sigfigs;

    /**
     * the snapshot length for the capture (typically 65535 or even more, but might be limited by the user).
     */
    private long snaplen;

    /**
     * link-layer header type.
     */
    private LinkType network;

    private final Pcap _parent;

    public Header(KaitaiStream _io, Pcap _parent) {
        super(_io);
        this._parent = _parent;
        _read();
    }

    private void _read() {
        this.magicNumber = this._io.readBytes(4);
        if (!(Arrays.equals(magicNumber(), new byte[] {-44, -61, -78, -95}))) {
            throw new KaitaiStream.ValidationNotEqualError(new byte[] {-44, -61, -78, -95}, magicNumber(), _io(), "/types/header/seq/0");
        }
        this.versionMajor = this._io.readU2le();
        this.versionMinor = this._io.readU2le();
        this.thiszone = this._io.readS4le();
        this.sigfigs = this._io.readU4le();
        this.snaplen = this._io.readU4le();
        this.network = LinkType.byId(this._io.readU4le());
    }

    public byte[] magicNumber() {
        return magicNumber;
    }

    public int versionMajor() {
        return versionMajor;
    }

    public int versionMinor() {
        return versionMinor;
    }

    /**
     * Correction time in seconds between UTC and the local
     * timezone of the following packet header timestamps.
     */
    public int thiszone() {
        return thiszone;
    }

    /**
     * In theory, the accuracy of time stamps in the capture; in
     * practice, all tools set it to 0.
     */
    public long sigfigs() {
        return sigfigs;
    }

    /**
     * The "snapshot length" for the capture (typically 65535 or
     * even more, but might be limited by the user), see: incl_len
     * vs. orig_len.
     */
    public long snaplen() {
        return snaplen;
    }

    /**
     * Link-layer header type, specifying the type of headers at
     * the beginning of the packet.
     */
    public LinkType network() {
        return network;
    }

    public Pcap _parent() {
        return _parent;
    }
}