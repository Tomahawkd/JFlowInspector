package io.tomahawkd.jflowinspector.execute;

import io.tomahawkd.jflowinspector.config.CommandlineDelegate;

public abstract class AbstractExecutor implements Executor {

    public AbstractExecutor() {

    }

    @Override
    public void execute(CommandlineDelegate delegate) throws Exception {
        throw new IllegalStateException("Not Implement yet.");
    }
}
