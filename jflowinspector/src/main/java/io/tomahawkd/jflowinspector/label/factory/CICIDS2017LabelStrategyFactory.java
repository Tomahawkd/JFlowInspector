package io.tomahawkd.jflowinspector.label.factory;

import io.tomahawkd.jflowinspector.flow.Flow;
import io.tomahawkd.jflowinspector.label.LabelStrategy;
import io.tomahawkd.jflowinspector.source.LocalFile;

@LabelFactory(name = "CICIDS2017LabelStrategyFactory", dataset = "CICIDS2017")
public class CICIDS2017LabelStrategyFactory extends LabelStrategyFactory {

    @Override
    public LabelStrategy getStrategy(LocalFile file) {
        if (file.filenameContains("Wednesday-WorkingHours")) {
            // 172.16.0.1 -> 192.168.10.50:80
            return f -> {
                if (f.connectBetween("172.16.0.1", Flow.PORT_ANY, "192.168.10.50", 80)) {
                    return "SLOWDOS";
                } else return "NORMAL";
            };
        } else if (file.filenameContains("Friday-WorkingHours")) {
            return f -> {
                if (f.connectBetween("172.16.0.1", Flow.PORT_ANY, "192.168.10.50", 80)) {
                    return "DOS";
                } else return "NORMAL";
            };
        }

        return LabelStrategy.NONE;
    }
}
