package io.tomahawkd.jflowinspector.flow;

import io.tomahawkd.jflowinspector.execute.ExecutionMode;
import io.tomahawkd.jflowinspector.label.LabelStrategy;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FlowGenerator {
    public static final Logger logger = LogManager.getLogger(FlowGenerator.class);

    private final List<FlowGenListener> listeners;
    private LabelStrategy labelStrategy = LabelStrategy.DEFAULT;
    private final HashMap<String, Flow> currentFlows;

    private final long flowTimeOut;
    private final long flowActivityTimeOut;

    private int packetCounter;

    private final BiFunction<Flow, Long, Boolean> timeoutStrategy;

    private long flowCount = 0;

    public FlowGenerator(long flowTimeout, long activityTimeout, ExecutionMode mode) {
        super();
        this.flowTimeOut = flowTimeout;
        this.flowActivityTimeOut = activityTimeout;
        currentFlows = new HashMap<>();
        packetCounter = 0;
        listeners = new ArrayList<>();

        if (mode == ExecutionMode.FULL || mode == ExecutionMode.ONLINE) {
            this.timeoutStrategy = this::fullTimeout;
        } else {
            this.timeoutStrategy = this::samplingTimeout;
        }
    }

    public void addFlowListener(FlowGenListener listener) {
        listeners.add(listener);
    }

    public void setFlowLabelSupplier(LabelStrategy supplier) {
        labelStrategy = supplier;
    }

    public int getCurrentFlowCount() {
        return currentFlows.size();
    }

    public long getFlowCount() {
        return flowCount;
    }

    public boolean containsFlow(PacketInfo packet) {
        packetCounter++;
        if (packetCounter > 0x8000) {
            flushTimeoutFlows(packet.getTimestamp());
            packetCounter = 0;
        }

        if (this.currentFlows.containsKey(packet.fwdFlowId())) {
            packet.setFwd();
            return true;
        } else if (this.currentFlows.containsKey(packet.bwdFlowId())) {
            packet.setBwd();
            return true;
        } else {
            return false;
        }
    }

    public void addPacket(PacketInfo packet) {
        if (packet == null) return;

        logger.debug("Received packet with id {}", packet.getFlowId());
        String id = packet.getFlowId();
        Flow flow;

        if (!this.currentFlows.containsKey(id)) {
            flow = new Flow(packet, flowActivityTimeOut, labelStrategy);
            flow.setStatus(FlowStatus.NEW);
            currentFlows.put(id, flow);
        } else {
            flow = currentFlows.get(id);
        }

        FlowStatus flowStatus = flow.getStatus();
        boolean fwd = Arrays.equals(flow.getBasicInfo().src(), packet.getSrc());

        // initialization of a flow
        if (packet.getFlag(PacketInfo.FLAG_SYN)) {
            if (packet.getFlag(PacketInfo.FLAG_ACK)) {
                // SYN + ACK
                if (flowStatus != FlowStatus.SYNC_SENT) {
                    logger.warn("SYN ACK packet in status {}, maintain original status.", flowStatus);
                } else {
                    flow.setStatus(FlowStatus.SYNC_RECV);
                }
            } else {
                // only SYN
                if (flowStatus != FlowStatus.NEW) {
                    logger.warn("SYN packet in status {}, maintain original status.", flowStatus);
                } else {
                    flow.setStatus(FlowStatus.SYNC_SENT);
                }
            }

        } else if (packet.getFlag(PacketInfo.FLAG_RST)) {
            // avoid been filtered out
            if (flowStatus == FlowStatus.NEW) flow.setStatus(FlowStatus.ESTABLISHED);
            flow.addPacket(packet);
            currentFlows.put(id, flow);
            finishFlow(id, "Flag-RST");
        }else if (packet.getFlag(PacketInfo.FLAG_ACK)) {
            // only ACK

            if (flowStatus == FlowStatus.SYNC_RECV) {
                flow.setStatus(FlowStatus.ESTABLISHED);

            } else if (flowStatus == FlowStatus.NEW) {
                logger.warn("Initialized flow {} without SYN flag.", flow.getFlowId());

            } else if (flowStatus == FlowStatus.SYNC_SENT) {
                flow.setStatus(FlowStatus.ESTABLISHED);
                logger.warn("Missing SYN+ACK packet, set to {} forcibly", FlowStatus.ESTABLISHED);

            } else if (flowStatus == FlowStatus.FIN_WAIT) {
                int fin = fwd ? Flow.SRC_FIN_SEE : Flow.DST_FIN_SEE;
                int ack = fwd ? Flow.SRC_FIN_ACK : Flow.DST_FIN_ACK;

                if (flow.getFinStatusAt(fin)) {
                    flow.setFinStatusAt(ack);
                } else {
                    logger.warn("Flow {} {} FIN is missing while getting ACK to this FIN.",
                            flow.getFlowId(), fwd ? "Forward": "Backward");
                }

                if (flow.getFinStatusAt(Flow.ALL)) {
                    flow.addPacket(packet);
                    currentFlows.put(id, flow);
                    finishFlow(id, "Flag-FIN");
                    return;
                }
            } // else FlowStatus.ESTABLISHED, just add packet

        } else if (packet.getFlag(PacketInfo.FLAG_FIN)) {
            if (flowStatus != FlowStatus.FIN_WAIT) {
                // treat as ESTABLISHED
                if (flowStatus != FlowStatus.ESTABLISHED && flowStatus != FlowStatus.SYNC_RECV) {
                    logger.warn("FIN received at status {} which should not happen", flowStatus);
                }
                flow.setStatus(FlowStatus.FIN_WAIT);
            }

            int mask = fwd ? Flow.SRC_FIN_SEE : Flow.DST_FIN_SEE;
            if (flow.getFinStatusAt(mask)) {
                logger.warn("Received redundant FIN flag");
            } else {
                flow.setFinStatusAt(mask);
            }
        }

        flow.addPacket(packet);
        currentFlows.put(id, flow);
    }

    public void dumpLabeledCurrentFlow() {
        // treat the left flows as completed
        currentFlows.values().forEach(this::callback);
    }

    private void flushTimeoutFlows(long timestamp) {
        logger.debug("Flushing timeout flows.");
        List<Flow> list = currentFlows.values().stream()
                .filter(flow -> timeoutStrategy.apply(flow, timestamp))
                .collect(Collectors.toList());

        list.forEach(flow -> finishFlow(flow.getFlowId(), "Timeout"));
    }

    private boolean samplingTimeout(Flow flow, long timestamp) {
        return timestamp - flow.getFlowStartTime() > this.flowTimeOut;
    }

    private boolean fullTimeout(Flow flow, long timestamp) {
        return timestamp - flow.getFlowLastSeen() > this.flowTimeOut;
    }

    private void finishFlow(String id, String type) {
        logger.info("Flow {} finalized by {}", id, type);
        logger.debug("Current flow count: {}", currentFlows.size());
        Flow flow = currentFlows.remove(id);
        flow.finalizeFlow();
        callback(flow);
    }

    private void callback(Flow flow) {
        flowCount++;
        listeners.forEach(l -> l.onFlowGenerated(flow));
    }
}
