package io.tomahawkd.jflowinspector.packet;

import com.google.common.primitives.Primitives;
import io.tomahawkd.jflowinspector.util.Utils;
import org.jetbrains.annotations.Nullable;
import org.jnetpcap.packet.format.FormatUtils;

import java.util.*;

public class PacketInfo {

    private final long id;
    private String flowId = "";
    private final Map<PacketFeature, Object> data;
    private long timestamp;

    // Cached Meta-data of the package
    // typically we use the map above to store data, however, to make it
    // more faster we use pre-defined fields instead
    private byte[] src;
    private byte[] dst;
    private int srcPort;
    private int dstPort;

    // tcp data
    private int tcpWindow;
    private int flags;
    private long seq;
    private long ack;

    public PacketInfo(long id) {
        data = new HashMap<>();
        this.id = id;
    }

    public void addFeature(PacketFeature feature, Object data) {
        if (data == null) return;
        if (feature.getType().isAssignableFrom(data.getClass())) {
            this.data.put(feature, data);
        } else throw new IllegalArgumentException(
                "Expecting type " + feature.getType().getName() +
                        " but receiving " + data.getClass().getName());
    }

    @Nullable
    @SuppressWarnings("all")
    public <T> T getFeature(PacketFeature feature, Class<T> type) {
        Object o = data.get(feature);
        if (o == null) return null;

        if (Primitives.wrap(type).isAssignableFrom(Primitives.wrap(feature.getType()))) {
            return (T) o;
        } else throw new IllegalArgumentException(
                "Expecting type " + feature.getType().getName() +
                        " but requesting " + type.getName());
    }

    public boolean getBoolFeature(PacketFeature feature, boolean defaultValue) {
        if (Boolean.class.isAssignableFrom(Primitives.wrap(feature.getType()))) {
            return Optional.ofNullable(
                    this.getFeature(feature, Boolean.class)
            ).orElse(defaultValue);
        } else return defaultValue;
    }

    public boolean getBoolFeature(PacketFeature feature) {
        return getBoolFeature(feature, false);
    }

    @SuppressWarnings("all")
    private <T> T unsafeGet(PacketFeature feature, Class<T> type) {
        return (T) Objects.requireNonNull(data.get(feature));
    }

    public void removeFeature(PacketFeature feature) {
        data.remove(feature);
    }

    public void finishParse() {
        this.src = unsafeGet(MetaFeature.SRC, byte[].class);
        this.dst = unsafeGet(MetaFeature.DST, byte[].class);
        this.srcPort = unsafeGet(MetaFeature.SRC_PORT, int.class);
        this.dstPort = unsafeGet(MetaFeature.DST_PORT, int.class);
        this.tcpWindow = unsafeGet(TcpPacketDelegate.Feature.TCP_WINDOW, int.class);
        this.flags = unsafeGet(TcpPacketDelegate.Feature.FLAG, int.class);
        this.seq = unsafeGet(TcpPacketDelegate.Feature.SEQ, long.class);
        this.ack = unsafeGet(TcpPacketDelegate.Feature.ACK, long.class);
        generateFlowId();
    }

    private void generateFlowId() {
        this.flowId = this.getSourceIP() + "-" + this.getDestinationIP() + "-" +
                this.srcPort + "-" + this.dstPort;
    }

    public String getSourceIP() {
        return FormatUtils.ip(this.src);
    }

    public String getDestinationIP() {
        return FormatUtils.ip(this.dst);
    }

    public String fwdFlowId() {
        return this.getSourceIP() + "-" + this.getDestinationIP() + "-" +
                this.srcPort + "-" + this.dstPort;
    }

    public String bwdFlowId() {
        return this.getDestinationIP() + "-" + this.getSourceIP() + "-" +
                this.dstPort + "-" + this.srcPort;
    }

    public PacketInfo setFwd() {
        this.flowId = fwdFlowId();
        return this;
    }

    public PacketInfo setBwd() {
        this.flowId = bwdFlowId();
        return this;
    }

    public long getId() {
        return id;
    }

    public String getFlowId() {
        return flowId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getSrc() {
        return Arrays.copyOf(this.src, this.src.length);
    }

    public byte[] getDst() {
        return Arrays.copyOf(this.dst, this.dst.length);
    }

    public int getSrcPort() {
        return this.srcPort;
    }

    public int getDstPort() {
        return this.dstPort;
    }

    public long getPayloadBytes() {
        return this.unsafeGet(MetaFeature.PAYLOAD_LEN, int.class);
    }

    public long getHeaderBytes() {
        return this.unsafeGet(MetaFeature.HEADER_LEN, int.class);
    }

    public int getTcpWindow() {
        return tcpWindow;
    }

    public boolean getFlag(int id) {
        return (flags & id) != 0;
    }

    public long seq() {
        return seq;
    }

    public long ack() {
        return ack;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PacketInfo[");
        builder.append("flowID: ").append(flowId).append(", ");
        for (Map.Entry<PacketFeature, Object> entry: data.entrySet()){
            builder.append(entry.getKey().toString()).append(": ")
                    .append(Utils.convertToString(entry.getValue())).append(", ");
        }
        return builder.append("]").toString();
    }

    // Copied from org.jnetpcap.protocol.tcpip.Tcp
    /**
     * The Constant FLAG_ACK.
     */
    public static final int FLAG_ACK = 0x10;

    /**
     * The Constant FLAG_CWR.
     */
    public static final int FLAG_CWR = 0x80;

    /**
     * The Constant FLAG_ECE.
     */
    public static final int FLAG_ECE = 0x40;

    /**
     * The Constant FLAG_FIN.
     */
    public static final int FLAG_FIN = 0x01;

    /**
     * The Constant FLAG_PSH.
     */
    public static final int FLAG_PSH = 0x08;

    /**
     * The Constant FLAG_RST.
     */
    public static final int FLAG_RST = 0x04;

    /**
     * The Constant FLAG_SYN.
     */
    public static final int FLAG_SYN = 0x02;

    /**
     * The Constant FLAG_URG.
     */
    public static final int FLAG_URG = 0x20;
}
