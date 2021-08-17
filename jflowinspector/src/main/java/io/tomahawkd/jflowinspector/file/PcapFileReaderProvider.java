package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.config.ConfigManager;
import io.tomahawkd.config.util.ClassManager;
import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum PcapFileReaderProvider {

    INSTANCE;

    private static Logger logger;

    PcapFileReaderProvider() {
        init();
    }

    private void init() {
        logger = LogManager.getLogger(PcapFileReaderProvider.class);
    }

    public PcapFileReader newReader(Path file) throws IOException {
        CommandlineDelegate delegate = ConfigManager.get().getDelegateByType(CommandlineDelegate.class);
        if (delegate != null && delegate.useOldParser()) {
            Path jarPath = delegate.getOldParserPath();
            URL url = new URL("file:" + jarPath.toAbsolutePath());

            List<ClassLoader> list = new ArrayList<>();
            list.add(new URLClassLoader(new URL[]{url}));
            Set<Class<? extends PcapFileReader>> classes =
                    ClassManager.createManager(list)
                            .loadClasses(PcapFileReader.class, "io.tomahawkd.jflowinspector.file.jnetpcap");

            if (classes.isEmpty()) {
                logger.error("Jnetpcap File Reader is not found, fall back to bundled readers.");
            } else {
                Class<? extends PcapFileReader> reader = classes.stream().findFirst().get();
                try {
                    return reader.getConstructor(Path.class).newInstance(file);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    logger.error("Jnetpcap File Reader creation failed, fall back to bundled readers.");
                }
            }
        }

        return new BundledPcapFileReader(file);
    }
}
