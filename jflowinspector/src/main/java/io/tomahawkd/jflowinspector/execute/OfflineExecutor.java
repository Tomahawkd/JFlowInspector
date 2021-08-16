package io.tomahawkd.jflowinspector.execute;

import io.tomahawkd.jflowinspector.config.CommandlineDelegate;
import io.tomahawkd.jflowinspector.flow.Flow;
import io.tomahawkd.jflowinspector.flow.FlowGenerator;
import io.tomahawkd.jflowinspector.label.LabelStrategy;
import io.tomahawkd.jflowinspector.label.LabelStrategyFactoryManager;
import io.tomahawkd.jflowinspector.packet.PacketInfo;
import io.tomahawkd.jflowinspector.packet.PcapReader;
import io.tomahawkd.jflowinspector.source.LocalFile;
import io.tomahawkd.jflowinspector.source.LocalFiles;
import io.tomahawkd.jflowinspector.source.LocalMultiFile;
import io.tomahawkd.jflowinspector.source.LocalSingleFile;
import io.tomahawkd.jflowinspector.thread.PacketDispatcher;
import io.tomahawkd.jflowinspector.thread.SimplePacketDispatcher;
import io.tomahawkd.jflowinspector.util.SimpleWriter;
import io.tomahawkd.jflowinspector.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@WithMode({ExecutionMode.SAMPLING, ExecutionMode.FULL})
public class OfflineExecutor extends AbstractExecutor {

    private static final Logger logger = LogManager.getLogger(OfflineExecutor.class);

    public OfflineExecutor() {
        super();
    }

    @Override
    public void execute(CommandlineDelegate delegate) throws Exception {

        Map<LocalFile, Path> inputOutputPaths = delegate.getInputOutputPaths();
        boolean oneFile = delegate.isOneFile();
        Path oneOutputPath = delegate.getOneFilePath();

        if (oneFile) {
            initFile(oneOutputPath);
            inputOutputPaths.forEach((inputFile, ignored) -> {
                logger.info("Start Processing {}", inputFile.getFileName());
                readPcapFile(inputFile, oneOutputPath, delegate);
            });
        } else {
            inputOutputPaths.forEach((inputFile, outputPath) -> {
                initFile(outputPath);
                logger.info("Start Processing {}", inputFile.getFileName());
                readPcapFile(inputFile, outputPath, delegate);
            });
        }
    }

    private void readPcapFile(LocalFile inputFile, Path outputPath, CommandlineDelegate delegate) {
        if (inputFile == null || outputPath == null) {
            logger.fatal("Got a null path.");
            throw new RuntimeException("Got a null path.");
        }

        if (!LocalFiles.exists(inputFile) || !Files.exists(outputPath)) {
            logger.fatal("File not found. Status: input({}), output({})",
                    LocalFiles.exists(inputFile), Files.exists(outputPath));
            throw new RuntimeException("File not found.");
        }

        String fileName = inputFile.getFileName();
        System.out.printf("Working on... %s%n", fileName);

        // setting up
        SimpleWriter writer = new SimpleWriter(outputPath);
        LabelStrategy strategy = LabelStrategyFactoryManager.get().getStrategy(inputFile);
        SimplePacketDispatcher dispatcher =
                new SimplePacketDispatcher(delegate.getFlowThreadCount(), delegate.getFlowQueueSize(), () -> {

            FlowGenerator flowGen =
                    new FlowGenerator(delegate.getFlowTimeout(), delegate.getActivityTimeout(), delegate.getMode());
            flowGen.setFlowLabelSupplier(strategy);

            // data export
            flowGen.addFlowListener(flow -> {
                try {
                    writer.write(flow.exportData());
                } catch (IOException e) {
                    logger.error("IO Exception while writing data", e);
                }
            });
            return flowGen;
        });

        dispatcher.start();
        try {
            if (inputFile instanceof LocalMultiFile) {
                LocalMultiFile file = (LocalMultiFile) inputFile;
                file.getSegments().forEach(localSingleFile -> readData(dispatcher, localSingleFile.getFilePath()));
            } else if (inputFile instanceof LocalSingleFile) {
                readData(dispatcher, inputFile.getFilePath());
            }

            System.out.printf("File %s read complete, flushing unterminated flows.%n", inputFile.getFileName());
            dispatcher.stop();

            long waitTime = System.currentTimeMillis();
            while (dispatcher.running()) {
                System.out.printf("Termination: %s -> %d flows\r", inputFile.getFileName(), dispatcher.getFlowCount());
                if (System.currentTimeMillis() - waitTime > 1000 * 60 * 10) {
                    dispatcher.forceStop();
                }
            }

            System.out.printf("%s is done. total %d flows %n", inputFile.getFileName(), dispatcher.getFlowCount());
            System.out.println(Utils.DividingLine);
        } catch (Exception e) {
            System.out.println();
            dispatcher.forceStop();
            throw e;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("Writer close with error", e);
            }
        }
    }

    private void readData(PacketDispatcher dispatcher, Path filePath) {
        PcapReader pcapReader = new PcapReader(filePath);
        long nTotal = 0;
        long nValid = 0;
        long nInvalid = 0;
        while (true) {
            try {
                PacketInfo basicPacket = pcapReader.nextPacket();
                nTotal++;
                if (basicPacket != null) {
                    dispatcher.dispatch(basicPacket);
                    nValid++;
                } else nInvalid++;

                System.out.printf("%s -> Total: %d,Valid: %d,Discarded: %d, %d flows \r",
                        filePath.getFileName(), nTotal, nValid, nInvalid, dispatcher.getFlowCount());
            } catch (EOFException e) {
                break;
            } catch (InterruptedException e) {
                logger.error("Waiting thread is interrupted.", e);
                break;
            }
        }

        // clear line
        System.out.println();
        System.out.printf("Packet stats: Total=%d,Valid=%d,Discarded=%d%n", nTotal, nValid, nInvalid);
    }

    private void initFile(Path file) {
        if (Files.exists(file)) {
            logger.info("File already exists. Removing...");
            try {
                Files.delete(file);
            } catch (IOException e) {
                logger.fatal("Save file {} can not be deleted.", file.toString(), e);
                throw new RuntimeException("Save file {} can not be deleted.", e);
            }
        }

        try {
            logger.info("Creating file {}...", file.getFileName().toString());
            Utils.initFile(file, Flow.getHeaders());
        } catch (IOException e) {
            logger.fatal("Failed to create file");
            throw new RuntimeException(e);
        }
    }
}
