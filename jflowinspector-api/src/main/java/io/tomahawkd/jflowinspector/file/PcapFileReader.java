package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.jflowinspector.extension.ParameterizedExtensionPoint;

public interface PcapFileReader extends ParameterizedExtensionPoint {

    boolean hasNext();

    /**
     * @return packet. If the packet is null, it indicates the EOF.
     */
    PcapPacket next();
}
