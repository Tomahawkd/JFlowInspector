package io.tomahawkd.jflowinspector.flow;

public enum FlowStatus {

    /**
     * A new flow with no packet
     */
    NEW,

    /**
     * The first SYN was seen, wait for opposite SYN
     */
    SYNC_SENT,

    /**
     * Two SYN was seen, wait for ACK
     */
    SYNC_RECV,

    /**
     * Communicating
     */
    ESTABLISHED,

    /**
     * The FIN was seen, start fin procedure
     */
    FIN_WAIT;
}
