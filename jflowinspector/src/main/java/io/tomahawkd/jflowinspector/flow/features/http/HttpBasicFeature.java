package io.tomahawkd.jflowinspector.flow.features.http;

import io.tomahawkd.jflowinspector.flow.features.Feature;
import io.tomahawkd.jflowinspector.flow.features.FeatureType;
import io.tomahawkd.jflowinspector.flow.features.FlowFeatureTag;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Feature(name = "HttpFeature", tags = {
        FlowFeatureTag.request_packet_count,
        FlowFeatureTag.invalid_request_header_count,
        FlowFeatureTag.main_page_count,
        FlowFeatureTag.query_request_count,
        FlowFeatureTag.query_length_avg,
        FlowFeatureTag.query_length_std,
        FlowFeatureTag.query_length_max,
        FlowFeatureTag.query_length_min,
        FlowFeatureTag.query_length_total,
        FlowFeatureTag.content_length_avg,
        FlowFeatureTag.content_length_std,
        FlowFeatureTag.content_length_max,
        FlowFeatureTag.content_length_min,
        FlowFeatureTag.content_length_total,
        FlowFeatureTag.req_content_length_avg,
        FlowFeatureTag.req_content_length_std,
        FlowFeatureTag.req_content_length_max,
        FlowFeatureTag.req_content_length_min,
        FlowFeatureTag.req_content_length_total,
        FlowFeatureTag.res_content_length_avg,
        FlowFeatureTag.res_content_length_std,
        FlowFeatureTag.res_content_length_max,
        FlowFeatureTag.res_content_length_min,
        FlowFeatureTag.res_content_length_total,
        FlowFeatureTag.keep_alive_packet_ratio,
        FlowFeatureTag.method_get_count,
        FlowFeatureTag.method_post_count,
        FlowFeatureTag.header_element_avg,
        FlowFeatureTag.header_element_std,
        FlowFeatureTag.header_element_min,
        FlowFeatureTag.header_element_max,
        FlowFeatureTag.header_element_total,
}, ordinal = 1, type = FeatureType.HTTP)
public class HttpBasicFeature extends HttpFlowFeature {

    private static final Logger logger = LogManager.getLogger(HttpBasicFeature.class);

    private long access_main_page_count = 0;
    private long invalid_request_header = 0L;
    private final SummaryStatistics query_stat = new SummaryStatistics();
    private final SummaryStatistics content_length = new SummaryStatistics();
    private final SummaryStatistics content_length_req = new SummaryStatistics();
    private final SummaryStatistics content_length_res = new SummaryStatistics();
    private long keepAliveCount = 0L;
    private long getCount = 0;
    private long postCount = 0;
    private final SummaryStatistics headerElement_state = new SummaryStatistics();

    public HttpBasicFeature(HttpFeatureAdapter httpFeature) {
        super(httpFeature);
    }

    @Override
    public void addGenericPacket(PacketInfo info, boolean isRequest) {
        int contentLength = Optional.ofNullable(
                info.getFeature(HttpPacketFeature.CONTENT_LEN, Integer.class))
                .orElse(0);
        content_length.addValue(contentLength);

        headerElement_state.addValue(Optional.ofNullable(
                        info.getFeature(HttpPacketFeature.HEADER_LINE_COUNT, Integer.class))
                .orElse(0));

        if (isRequest) {
            content_length_req.addValue(contentLength);
        } else {
            content_length_res.addValue(contentLength);
        }
    }

    @Override
    public void addRequestPacket(PacketInfo info) {
        if (info.getBoolFeature(HttpPacketFeature.INVALID)) {
            invalid_request_header++;
            return;
        }

        String path = info.getFeature(HttpPacketFeature.PATH, String.class);
        if (path != null) {
            if (path.equals("/") || path.contains("index.html")) access_main_page_count++;

            // we dont care its protocol and host
            try {
                URL url = new URL("http://host" + path);
                String query = url.getQuery();
                if (query != null) query_stat.addValue(query.length());
            } catch (MalformedURLException e) {
                logger.warn("Invalid request path {}.", path);
            }
        }

        String connection = info.getFeature(HttpPacketFeature.CONNECTION, String.class);
        if (connection != null) {
            if (connection.equalsIgnoreCase("keep-alive")) keepAliveCount++;
        }

        String method = info.getFeature(HttpPacketFeature.METHOD, String.class);
        if (method != null) {
            if (method.equalsIgnoreCase("get")) getCount++;
            else if (method.equalsIgnoreCase("post")) postCount++;
        }
    }

    @Override
    public String exportData() {
        StringBuilder builder = new StringBuilder();
        long requestPacketCount = getRequestPacketCount();
        builder.append(requestPacketCount).append(SEPARATOR); // FlowFeatureTag.request_packet_count
        builder.append(invalid_request_header).append(SEPARATOR); // FlowFeatureTag.invalid_request_header_count
        builder.append(access_main_page_count).append(SEPARATOR); // FlowFeatureTag.main_page_count,
        builder.append(query_stat.getN()).append(SEPARATOR); // FlowFeatureTag.query_request_count,
        buildLength(builder, query_stat);
        buildLength(builder, content_length);
        buildLength(builder, content_length_req);
        buildLength(builder, content_length_res);

        if (requestPacketCount > 0) builder.append((double)keepAliveCount/requestPacketCount);
        else builder.append(0);
        builder.append(SEPARATOR); // FlowFeatureTag.keep_alive_packet_ratio,

        builder.append(getCount).append(SEPARATOR); // FlowFeatureTag.method_get_count,
        builder.append(postCount).append(SEPARATOR); // FlowFeatureTag.method_post_count,
        buildLength(builder, headerElement_state);
        return builder.toString();
    }

    private void buildLength(StringBuilder builder, SummaryStatistics lengthStat) {
        if (lengthStat.getN() > 0) {
            builder.append(lengthStat.getMean()).append(SEPARATOR); // avg,
            builder.append(lengthStat.getStandardDeviation()).append(SEPARATOR); // std,
            builder.append(lengthStat.getMax()).append(SEPARATOR); // max,
            builder.append(lengthStat.getMin()).append(SEPARATOR); // min,
            builder.append(lengthStat.getSum()).append(SEPARATOR); // total,
        } else {
            addZeroesToBuilder(builder, 5);
        }
    }

    public long getRequestPacketCount() {
        return content_length.getN();
    }
}
