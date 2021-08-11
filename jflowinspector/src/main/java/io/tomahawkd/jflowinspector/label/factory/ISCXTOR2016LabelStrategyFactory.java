package io.tomahawkd.jflowinspector.label.factory;

import io.tomahawkd.jflowinspector.label.LabelStrategy;
import io.tomahawkd.jflowinspector.source.LocalFile;

@LabelFactory(name = "ISCXTOR2016LabelStrategyFactory", dataset = "ISCXTor2016")
public class ISCXTOR2016LabelStrategyFactory extends LabelStrategyFactory {

    @Override
    public LabelStrategy getStrategy(LocalFile file) {
        if (file.filenameContains("browsing")) {
            return f -> "NORMAL";
        } else return LabelStrategy.NONE;
    }
}
