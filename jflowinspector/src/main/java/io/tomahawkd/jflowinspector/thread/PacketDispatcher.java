package io.tomahawkd.jflowinspector.thread;

import io.tomahawkd.jflowinspector.packet.PacketInfo;

public interface PacketDispatcher {

    void dispatch(PacketInfo info) throws InterruptedException;

    long getFlowCount();
}
