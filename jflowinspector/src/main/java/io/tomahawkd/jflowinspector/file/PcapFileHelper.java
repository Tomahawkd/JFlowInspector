package io.tomahawkd.jflowinspector.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PcapFileHelper {

    public static PcapMagicNumber getMagicNumberFromFile(Path file) throws IOException {
        FileChannel fc = FileChannel.open(file, StandardOpenOption.READ);
        byte[] magicNumber = new byte[4];
        fc.read(ByteBuffer.wrap(magicNumber));
        fc.close();

        return PcapMagicNumber.getTypeBySignature(magicNumber);
    }

    public static boolean isPcapFile(Path file) throws IOException {
        PcapMagicNumber sign = getMagicNumberFromFile(file);
        return sign == PcapMagicNumber.PCAP || sign == PcapMagicNumber.PCAPNG;
    }
}
