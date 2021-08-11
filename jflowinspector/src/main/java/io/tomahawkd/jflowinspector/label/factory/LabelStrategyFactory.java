package io.tomahawkd.jflowinspector.label.factory;

import io.tomahawkd.jflowinspector.label.LabelStrategy;
import io.tomahawkd.jflowinspector.source.LocalFile;

public abstract class LabelStrategyFactory {

    public LabelStrategy getStrategy(LocalFile file) {
        return LabelStrategy.NONE;
    }
}
