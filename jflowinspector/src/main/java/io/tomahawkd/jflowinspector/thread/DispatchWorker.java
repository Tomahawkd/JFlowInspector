package io.tomahawkd.jflowinspector.thread;

public interface DispatchWorker extends Runnable {

    long getWorkload();

    int getQueueSize();

    void run();

    void close();

    void forceClose();
}
