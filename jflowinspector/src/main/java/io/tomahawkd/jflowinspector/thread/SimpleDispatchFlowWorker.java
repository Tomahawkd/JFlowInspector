package io.tomahawkd.jflowinspector.thread;

import io.tomahawkd.jflowinspector.flow.FlowGenerator;
import io.tomahawkd.jflowinspector.packet.PacketInfo;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDispatchFlowWorker implements DispatchFlowWorker {

    private boolean waiting;
    private final Dispatcher dispatcher;
    private final int queueSize;

    private final FlowGenerator flowGenerator;
    private final AtomicBoolean working;

    private final Deque<PacketInfo> queue;
    private final AtomicInteger queueCount;

    public SimpleDispatchFlowWorker(Dispatcher dispatcher, int queueSize, FlowGenerator flowGenerator) {
        this.dispatcher = dispatcher;
        this.queueSize = queueSize;
        this.flowGenerator = flowGenerator;
        this.working = new AtomicBoolean(false);
        this.queue = new ConcurrentLinkedDeque<>();
        this.queueCount = new AtomicInteger(0);
    }

    @Override
    public boolean containsFlow(PacketInfo info) {
        synchronized (flowGenerator) {
            return flowGenerator.containsFlow(info);
        }
    }

    @Override
    public void accept(PacketInfo info) throws InterruptedException {
        if (!this.working.get()) return;
        synchronized (this.queueCount) {
            int current = queueCount.incrementAndGet();
            if (current > this.queueSize) {
                waiting = true;
                dispatcher.wait();
            }

            queue.add(info);
            synchronized (this) {
                notify();
            }
        }
    }

    @Override
    public long getWorkload() {
        synchronized (flowGenerator) {
            return flowGenerator.getCurrentFlowCount();
        }
    }

    public int getQueueSize() {
        synchronized (this.queueCount) {
            return this.queueCount.get();
        }
    }

    @Override
    public long getFlowCount() {
        synchronized (flowGenerator) {
            return flowGenerator.getFlowCount();
        }
    }

    @Override
    public void updateTimestamp(long ts) {
        synchronized (flowGenerator) {
            flowGenerator.updateTimestamp(ts);
        }
    }

    private PacketInfo getPacketFromQueue() {
        synchronized (this.queueCount) {
            PacketInfo info = queue.pop();
            int current = this.queueCount.decrementAndGet();
            if (waiting && current < this.queueSize) {
                waiting = false;
                dispatcher.notify();
            }

            return info;
        }
    }

    @Override
    public void run() {
        this.working.set(true);

        while (true) {
            if (!queue.isEmpty()) {
                PacketInfo info = getPacketFromQueue();
                synchronized (this.flowGenerator) {
                    flowGenerator.addPacket(info);
                }
            } else {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            synchronized (this.working) {
                if (!this.working.get()) break;
            }
        }

        while (!queue.isEmpty()) {
            PacketInfo info = getPacketFromQueue();
            synchronized (this.flowGenerator) {
                flowGenerator.addPacket(info);
            }
        }

        synchronized (this.flowGenerator) {
            flowGenerator.dumpLabeledCurrentFlow();
        }
    }

    @Override
    public void close() {
        synchronized (this.working) {
            this.working.set(false);
        }

        synchronized (this) {
            notify();
        }
    }

    @Override
    public void forceClose() {
        this.queue.clear();
        synchronized (this.queueCount) {
            this.queueCount.set(0);
        }
        close();
    }
}
