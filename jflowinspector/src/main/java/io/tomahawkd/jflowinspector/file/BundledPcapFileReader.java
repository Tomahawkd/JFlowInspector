package io.tomahawkd.jflowinspector.file;

import io.tomahawkd.jflowinspector.file.pcap.Pcap;
import io.tomahawkd.jflowinspector.file.pcapng.Pcapng;

import java.io.IOException;
import java.nio.file.Path;

@Reader(name = DefaultPcapFileReaderName.DEFAULT)
public class BundledPcapFileReader extends AbstractPcapFileReader {

    private final PcapFileReader reader;

    public BundledPcapFileReader(Path file) throws IOException {
        super(file);
        PcapMagicNumber id = PcapFileHelper.getMagicNumberFromFile(file);
        if (id == PcapMagicNumber.PCAP) this.reader = Pcap.fromFile(file);
        else if (id == PcapMagicNumber.PCAPNG) this.reader = Pcapng.fromFile(file);
        else {
            // Unknown
            throw new IllegalArgumentException("The file is not a PCAP file or PCAPNG file");
        }
    }

    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    @Override
    public PcapPacket next() {
        return reader.next();
    }
}
