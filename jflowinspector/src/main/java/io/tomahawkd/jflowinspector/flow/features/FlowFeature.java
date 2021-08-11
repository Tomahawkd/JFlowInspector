package io.tomahawkd.jflowinspector.flow.features;

import io.tomahawkd.jflowinspector.packet.PacketInfo;

public interface FlowFeature {

    String SEPARATOR = ",";

    String headers();

    String exportData();

    int columnCount();

    void addPacket(PacketInfo info, boolean fwd);

    void postAddPacket(PacketInfo info);

    void finalizeFlow();
}
