package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.config.ConfigManager;
import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import io.tomahawkd.jflowinspector.extension.ExtensionPoint;
import io.tomahawkd.jflowinspector.extension.ParameterizedExtensionHandler;
import io.tomahawkd.jflowinspector.extension.ParameterizedExtensionPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class PcapFileReaderProvider implements ParameterizedExtensionHandler {

    private static final Logger logger = LogManager.getLogger(PcapFileReaderProvider.class);
    private Class<? extends PcapFileReader> readerClass;

    public PcapFileReaderProvider() {
    }

    public PcapFileReader newReader(Path file) throws IOException {

        if (readerClass == null) {
            logger.error("File Reader is not found, fall back to bundled reader.");
            return new BundledPcapFileReader(file);
        }

        try {
            Constructor<? extends PcapFileReader> c = readerClass.getConstructor(Path.class);
            c.setAccessible(true);
            return c.newInstance(file);
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            logger.error("Jnetpcap File Reader creation failed, fall back to bundled reader.");
        }

        return new BundledPcapFileReader(file);
    }

    @Override
    public boolean canAccept(Class<? extends ExtensionPoint> clazz) {
        return PcapFileReader.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean accept(Class<? extends ParameterizedExtensionPoint> extension) {
        Reader reader = extension.getAnnotation(Reader.class);
        if (reader == null) {
            logger.warn("Reader annotation not found in class {}", extension);
            return false;
        }

        String name = reader.name();
        if (name.equalsIgnoreCase(ConfigManager.get().getDelegateByType(CommandlineDelegate.class).getParser())) {
            this.readerClass = (Class<? extends PcapFileReader>) extension;
        }

        // just accept the unused class but ignore it
        return true;
    }
}
