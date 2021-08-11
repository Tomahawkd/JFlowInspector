package io.tomahawkd.jflowinspector.execute;

import io.tomahawkd.jflowinspector.config.CommandlineDelegate;

public interface Executor {

    void execute(CommandlineDelegate delegate) throws Exception;
}
