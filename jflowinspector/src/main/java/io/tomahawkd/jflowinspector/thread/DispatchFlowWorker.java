package io.tomahawkd.jflowinspector.thread;

import io.tomahawkd.jflowinspector.packet.PacketInfo;

public interface DispatchFlowWorker extends DispatchWorker {

    boolean containsFlow(PacketInfo info);

    void accept(PacketInfo info);

    long getWorkload();

    long getFlowCount();

    void updateTimestamp(long ts);

    void run();

    void close();

    void forceClose();
}
