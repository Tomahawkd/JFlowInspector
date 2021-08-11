package io.tomahawkd.jflowinspector.file;

import java.nio.file.Path;

public abstract class AbstractPcapFileReader implements PcapFileReader {

    protected final Path file;

    public AbstractPcapFileReader(Path file) {
        this.file = file;
    }
}
