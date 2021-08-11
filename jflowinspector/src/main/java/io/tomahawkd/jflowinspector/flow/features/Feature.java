package io.tomahawkd.jflowinspector.flow.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {

    String name();

    FlowFeatureTag[] tags();

    boolean manual() default false;

    int ordinal();

    FeatureType type() default FeatureType.UNKNOWN;
}
