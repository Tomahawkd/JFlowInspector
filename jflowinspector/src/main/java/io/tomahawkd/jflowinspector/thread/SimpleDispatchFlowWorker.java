package io.tomahawkd.jflowinspector.thread;

import io.tomahawkd.jflowinspector.flow.FlowGenerator;
import io.tomahawkd.jflowinspector.packet.PacketInfo;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDispatchFlowWorker implements DispatchFlowWorker {

    private final FlowGenerator flowGenerator;
    private final AtomicBoolean working;

    private final Deque<PacketInfo> queue;
    private final AtomicInteger queueCount;

    public SimpleDispatchFlowWorker(FlowGenerator flowGenerator) {
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
    public void accept(PacketInfo info) {
        if (!this.working.get()) return;
        queue.add(info);
        synchronized (this.queueCount) {
            queueCount.incrementAndGet();
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
    public void run() {
        this.working.set(true);

        while (true) {
            if (!queue.isEmpty()) {
                synchronized (this.flowGenerator) {
                    flowGenerator.addPacket(queue.pop());
                }

                synchronized (this.queueCount) {
                    this.queueCount.decrementAndGet();
                }
            }

            synchronized (this.working) {
                if (!this.working.get()) break;
            }
        }

        while (!queue.isEmpty()) {
            synchronized (this.flowGenerator) {
                flowGenerator.addPacket(queue.pop());
            }

            synchronized (this.queueCount) {
                this.queueCount.decrementAndGet();
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
    }

    @Override
    public void forceClose() {
        this.queue.clear();
        close();
    }
}
