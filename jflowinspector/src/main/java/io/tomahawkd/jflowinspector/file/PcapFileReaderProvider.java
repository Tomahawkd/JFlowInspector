package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.config.ConfigManager;
import io.tomahawkd.config.util.ClassManager;
import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import io.tomahawkd.jflowinspector.file.pcap.Pcap;
import io.tomahawkd.jflowinspector.file.pcapng.Pcapng;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    private PcapMagicNumber getMagicNumberFromFile(Path file) throws IOException {
        FileChannel fc = FileChannel.open(file, StandardOpenOption.READ);
        byte[] magicNumber = new byte[4];
        fc.read(ByteBuffer.wrap(magicNumber));
        fc.close();

        return PcapMagicNumber.getTypeBySignature(magicNumber);
    }

    public boolean isPcapFile(Path file) throws IOException {
        PcapMagicNumber sign = getMagicNumberFromFile(file);
        return sign == PcapMagicNumber.PCAP || sign == PcapMagicNumber.PCAPNG;
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

        switch (getMagicNumberFromFile(file)) {
            case PCAP: return Pcap.fromFile(file);
            case PCAPNG: return Pcapng.fromFile(file);
        }

        // Unknown
        throw new IllegalArgumentException("The file is not a PCAP file or PCAPNG file");
    }
}
