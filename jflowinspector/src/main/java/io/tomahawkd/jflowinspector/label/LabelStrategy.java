package io.tomahawkd.jflowinspector.label;

import io.tomahawkd.jflowinspector.flow.Flow;

@FunctionalInterface
public interface LabelStrategy {

    String getLabel(Flow flow);

    LabelStrategy NONE = null;
    LabelStrategy DEFAULT = f -> "NO_LABEL";
}
