package io.tomahawkd.jflowinspector.flow.features;

import io.tomahawkd.jflowinspector.flow.Flow;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractFlowFeature implements FlowFeature {

    private FlowFeatureTag[] headers;
    protected final Flow flow;

    public AbstractFlowFeature(Flow flow) {
        Feature feature = Objects.requireNonNull(this.getClass().getAnnotation(Feature.class));
        this.headers = feature.tags();
        this.flow = flow;
    }

    protected void setHeaders(FlowFeatureTag... tags) {
        this.headers = tags;
    }

    @Override
    public String headers() {
        return Arrays.stream(headers).map(FlowFeatureTag::getName).reduce("", (r, s) -> r + s + ",");
    }

    @Override
    public String exportData() {
        return StringUtils.repeat("0,", columnCount());
    }

    @Override
    public int columnCount() {
        return headers.length;
    }

    protected final void addZeroesToBuilder(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.append(0).append(SEPARATOR);
        }
    }

    @Override
    public void postAddPacket(PacketInfo info) {

    }

    @Override
    public void finalizeFlow() {

    }

    protected final <T extends FlowFeature> T getDep(Class<T> depClass) {
        return flow.getDep(depClass);
    }

    protected final FlowBasicFeature getBasicInfo() {
        return flow.getBasicInfo();
    }
}
