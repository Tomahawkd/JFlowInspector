package io.tomahawkd.jflowinspector.flow.features.http;

import io.tomahawkd.config.util.ClassManager;
import io.tomahawkd.jflowinspector.flow.features.Feature;
import io.tomahawkd.jflowinspector.flow.features.FlowFeatureTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum HttpFeatureBuilder {

    INSTANCE;

    private static Logger logger;
    private static List<Class<? extends HttpFlowFeature>> cache;

    HttpFeatureBuilder() {
        init();
    }

    private void init() {
        logger = LogManager.getLogger(HttpFeatureBuilder.class);

        if (cache == null) {
            cache = new ArrayList<>();
            ClassManager.createManager(null)
                    .loadClasses(HttpFlowFeature.class, "io.tomahawkd.cic.flow.features.http")
                    .stream()
                    .filter(f -> !Modifier.isAbstract(f.getModifiers()))
                    .filter(f -> f.getAnnotation(Feature.class) != null)
                    .peek(f -> logger.debug("Loading class {}", f.getName()))
                    .sorted(Comparator.comparingInt(f -> f.getAnnotation(Feature.class).ordinal()))
                    .forEachOrdered(c -> cache.add(c));
        }
    }

    public List<FlowFeatureTag> addFeaturesAndGetTags(HttpFeatureAdapter parent, List<HttpFlowFeature> features) {
        List<FlowFeatureTag> tags = new ArrayList<>();
        cache.forEach(c -> {
            Feature feature = c.getAnnotation(Feature.class);
            if (!feature.manual()) {
                try {
                    logger.debug("Creating instance of class {}", c.getName());
                    HttpFlowFeature newFeature = c.getConstructor(HttpFeatureAdapter.class).newInstance(parent);
                    features.add(newFeature);
                } catch (NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException e) {
                    logger.error("Cannot create new instance of {}", c, e);
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    parent.getByType(c);
                } catch (IllegalArgumentException e) {
                    logger.error("A manually created feature {} is not found in the list.", c.getName());
                    throw e;
                }
            }
            tags.addAll(Arrays.asList(feature.tags()));
        });
        return tags;
    }
}
