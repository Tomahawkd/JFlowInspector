package io.tomahawkd.jflowinspector.thread;

public interface DispatchWorker extends Runnable {

    long getWorkload();

    void run();

    void close();

    void forceClose();
}
