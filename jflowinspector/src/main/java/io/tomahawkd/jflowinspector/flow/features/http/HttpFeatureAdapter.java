package io.tomahawkd.jflowinspector.flow.features.http;

import io.tomahawkd.config.ConfigManager;
import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import io.tomahawkd.jflowinspector.flow.Flow;
import io.tomahawkd.jflowinspector.flow.features.AbstractFlowFeature;
import io.tomahawkd.jflowinspector.flow.features.Feature;
import io.tomahawkd.jflowinspector.flow.features.FeatureType;
import io.tomahawkd.jflowinspector.flow.features.FlowFeatureTag;
import io.tomahawkd.jflowinspector.packet.HttpPreprocessPacketDelegate;
import io.tomahawkd.jflowinspector.packet.MetaFeature;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Feature(name = "HttpFeatures", tags = {}, ordinal = 8, type = FeatureType.HTTP)
public class HttpFeatureAdapter extends AbstractFlowFeature {

    private static final Logger logger = LogManager.getLogger(HttpFeatureAdapter.class);

    private final TcpReorderer fwdReorderer;
    private final TcpReorderer bwdReorderer;
    private final List<HttpFlowFeature> features;
    private final boolean disableReassembling;

    private final HttpPacketParser parser;

    private int httpPackets = 0;

    public HttpFeatureAdapter(Flow flow) {
        super(flow);
        features = new ArrayList<>();
        List<FlowFeatureTag> tags = HttpFeatureBuilder.INSTANCE.addFeaturesAndGetTags(this, features);
        super.setHeaders(tags.toArray(new FlowFeatureTag[0]));
        disableReassembling = ConfigManager.get().getDelegateByType(CommandlineDelegate.class).isDisableReassemble();
        if (disableReassembling) {
            parser = new HttpPacketParser();
            fwdReorderer = null;
            bwdReorderer = null;
        } else {
            parser = null;
            fwdReorderer = new TcpReorderer(this::acceptPacket);
            bwdReorderer = new TcpReorderer(this::acceptPacket);
        }
    }

    @Override
    public final void addPacket(PacketInfo info, boolean fwd) {
        if (disableReassembling) {
            if (!info.getBoolFeature(MetaFeature.READABLE)) return;
            String readableString = info.getFeature(HttpPreprocessPacketDelegate.Feature.PAYLOAD, String.class);
            HttpPacketParser.Status parsed = parser.parseFeatures(info, readableString, true);
            if (parsed == HttpPacketParser.Status.OK) {
                acceptPacket(info);
            } else {
                logger.warn("Discarded packet [{}]", info);
            }

        } else {
            TcpReorderer reorderer = fwd ? fwdReorderer : bwdReorderer;
            reorderer.addPacket(info);
        }
    }

    private void acceptPacket(PacketInfo info) {
        Boolean request = info.getFeature(HttpPacketFeature.REQUEST, Boolean.class);
        if (request == null) {
            logger.warn("Packet {} has no request tag, discarded", info.getFlowId());
            logger.warn("Packet Content: {}", info.toString());
            return;
        }

        for (HttpFlowFeature feature : features) {
            feature.addGenericPacket(info, request);
            if (request) feature.addRequestPacket(info);
            else feature.addResponsePacket(info);
        }
        httpPackets++;
    }

    @Override
    public final void finalizeFlow() {
        if (!disableReassembling) {
            // deal with incomplete packets
            fwdReorderer.finalizeFlow();
            bwdReorderer.finalizeFlow();
        }
    }

    @Override
    public String exportData() {
        StringBuilder builder = new StringBuilder();
        for (HttpFlowFeature item: features) {
            builder.append(item.exportData());
        }
        return builder.toString();
    }

    public final <T extends HttpFlowFeature> T getByType(Class<T> type) {
        for (HttpFlowFeature item: features) {
            if (item.getClass().equals(type)) return type.cast(item);
        }

        throw new IllegalArgumentException(type.getName() + " not found.");
    }

    public final int getHttpPackets() {
        return httpPackets;
    }

    public final Flow getFlow() {
        return flow;
    }
}
