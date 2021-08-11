package io.tomahawkd.jflowinspector.thread;

public interface Dispatcher {

    void start();

    void stop();

    void forceStop();

    boolean running();
}
