package io.tomahawkd.jflowinspector.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    public static final String LINE_SEP = System.lineSeparator();
    public static final String FLOW_SUFFIX = "_Flow.csv";
    public static final String DEFAULT_OUTPUT_FILENAME = "Result" + FLOW_SUFFIX;
    public static final String DividingLine = "-------------------------------------------------------------------------------";


    public static void initFile(Path path, String header) throws IOException {
        if (Files.exists(path)) return;
        Files.createFile(path);
        try (FileOutputStream output = new FileOutputStream(path.toFile())) {
            output.write((header + LINE_SEP).getBytes());
        }
    }

    public static String convertToString(Object data) {
        Class<?> type = data.getClass();
        StringBuilder builder = new StringBuilder();
        if (type.isArray()) {
            builder.append("[");
            for (int i = 0; i < Array.getLength(data); i++) {
                builder.append(Array.get(data, i)).append(", ");
            }
            builder.append("]");
        } else if (data instanceof Map) {
            builder.append("{");
            ((Map<?, ?>) data).forEach((k, v) -> builder.append(k).append(": ").append(v).append(", "));
            builder.append("}");
        } else if (data instanceof Collection) {
            builder.append("[");
            ((Collection<?>) data).forEach(v -> builder.append(v).append(", "));
            builder.append("]");
        } else {
            return data.toString();
        }

        return builder.toString();
    }
}
