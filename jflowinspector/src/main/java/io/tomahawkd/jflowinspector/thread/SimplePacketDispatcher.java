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
            workers.add(new SimpleDispatchFlowWorker(generatorFactory.get()));
        }
    }

    public void dispatch(PacketInfo info) {
        if (!this.working) return;

        // flow is processing
        for (DispatchWorker e : workers) {
            DispatchFlowWorker worker = (DispatchFlowWorker) e;
            if (worker.containsFlow(info)) {
                waitForWorker(worker);
                worker.accept(info);
                return;
            }
        }

        // new flow
        ((DispatchFlowWorker) getLowestWorkloadWorker()).accept(info);
    }

    public long getFlowCount() {
        return workers.stream().map(e -> (DispatchFlowWorker) e)
                .mapToLong(DispatchFlowWorker::getFlowCount).sum();
    }
}
