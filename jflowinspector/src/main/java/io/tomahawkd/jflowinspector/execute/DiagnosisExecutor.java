package io.tomahawkd.jflowinspector.execute;

import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import io.tomahawkd.jflowinspector.packet.PcapReader;
import io.tomahawkd.jflowinspector.source.LocalMultiFile;
import io.tomahawkd.jflowinspector.source.LocalSingleFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.nio.file.Path;
import java.time.Instant;

@WithMode(ExecutionMode.DIAGNOSIS)
public class DiagnosisExecutor extends AbstractExecutor {

    private static final Logger logger = LogManager.getLogger(DiagnosisExecutor.class);

    @Override
    public void execute(CommandlineDelegate delegate) throws Exception {
        delegate.getInputOutputPaths().forEach((inputFile, ignored) -> {
            logger.info("Start Processing {}", inputFile.getFileName());

            if (inputFile instanceof LocalMultiFile) {
                LocalMultiFile file = (LocalMultiFile) inputFile;
                file.getSegments().forEach(localSingleFile -> readData(localSingleFile.getFilePath()));
            } else if (inputFile instanceof LocalSingleFile) {
                readData(inputFile.getFilePath());
            }
        });
    }

    private void readData(Path filePath) {
        PcapReader pcapReader = new PcapReader(filePath);
        int nTotal = 0;
        int nValid = 0;
        int nInvalid = 0;
        int count = 0;
        double maxSpeed = 0;
        double minSpeed = 0;
        long startTime = Instant.now().getEpochSecond();
        long start = startTime;
        double speed = 0;
        while (true) {
            try {
                PacketInfo basicPacket = pcapReader.nextPacket();
                nTotal++;
                count++;
                if (basicPacket != null) {
                    nValid++;
                } else nInvalid++;

                long interval = Instant.now().getEpochSecond() - start;
                if (interval > 1) {
                    speed = (double) count / interval;
                    if (speed > maxSpeed) maxSpeed = speed;
                    else if (speed < minSpeed) minSpeed = speed;
                    count = 0;
                    start = Instant.now().getEpochSecond();
                }

                System.out.printf("%s -> Total: %d,Valid: %d,Discarded: %d Speed: %f pkts/s \r",
                        filePath.getFileName(), nTotal, nValid, nInvalid, speed);
            } catch (EOFException e) {
                break;
            }
        }
        long endTime = Instant.now().getEpochSecond();
        double avgSpeed = (double) nTotal / (endTime - startTime);

        // clear line
        System.out.println();
        System.out.printf("Packet stats: Total=%d,Valid=%d,Discarded=%d%n", nTotal, nValid, nInvalid);
        System.out.printf("Average Parsing Speed: %f%n", avgSpeed);
        System.out.printf("Max Parsing Speed: %f%n", maxSpeed);
        System.out.printf("Min Parsing Speed: %f%n", minSpeed);
    }
}
