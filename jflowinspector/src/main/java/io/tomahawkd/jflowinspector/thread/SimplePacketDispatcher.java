package io.tomahawkd.jflowinspector.thread;

import io.tomahawkd.jflowinspector.flow.FlowGenerator;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SimplePacketDispatcher extends AbstractDispatcher implements PacketDispatcher {

    public SimplePacketDispatcher(int threads, int size, Supplier<FlowGenerator> generatorFactory) {
        super(threads, size);
        for (int i = 0; i < threads; i++) {
            workers.add(new SimpleDispatchFlowWorker(this, size, generatorFactory.get()));
        }
    }

    public void dispatch(PacketInfo info) throws InterruptedException {
        if (!this.working) return;

        // flow is processing
        DispatchFlowWorker pending = null;
        for (DispatchWorker e : workers) {
            DispatchFlowWorker worker = (DispatchFlowWorker) e;
            worker.updateTimestamp(info.getTimestamp());
            if (pending == null && worker.containsFlow(info)) {
                pending = worker;
            }
        }

        // new flow
        if (pending == null) {
            pending = (DispatchFlowWorker) getLowestWorkloadWorker();
        }

        pending.accept(info);
    }

    public long getFlowCount() {
        return workers.stream().map(e -> (DispatchFlowWorker) e)
                .mapToLong(DispatchFlowWorker::getFlowCount).sum();
    }
}
