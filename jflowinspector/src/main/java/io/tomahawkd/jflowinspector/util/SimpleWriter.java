package io.tomahawkd.jflowinspector.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;


public class SimpleWriter {

    private static final Logger logger = LogManager.getLogger(SimpleWriter.class);

    private final FileOutputStream stream;

    public SimpleWriter(Path file) {
        this(file, true);
    }

    public SimpleWriter(Path file, boolean append) {
        Objects.requireNonNull(file, "Path cannot be null");
        try {
            this.stream = new FileOutputStream(file.toFile(), append);
        } catch (FileNotFoundException e) {
            logger.fatal("File {} is not found", file);
            throw new RuntimeException("File " + file + " is not found.", e);
        }
    }

    public synchronized void write(String line) throws IOException {
        stream.write((line + Utils.LINE_SEP).getBytes());
    }

    public void close() throws IOException {
        if (stream != null) stream.close();
    }
}
