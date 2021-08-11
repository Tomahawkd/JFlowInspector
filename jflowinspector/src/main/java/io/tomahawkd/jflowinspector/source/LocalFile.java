package io.tomahawkd.jflowinspector.source;

import java.nio.file.Path;

public interface LocalFile {

    String getFileName();

    Path getFilePath();

    boolean exists();

    boolean filenameContains(CharSequence seq);
}
