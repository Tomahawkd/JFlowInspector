package io.tomahawkd.jflowinspector.file.protocols.tcp.option;

public enum TcpOptionType {

    EOL(0),
    NOP(1),
    MSS(2),
    WINDOW_SCALE(3),
    SACK_PERMIT(4),
    SACK(5),
    TIMESTAMPS(8),

    // since type is 1 byte, we use 256 to represent UNKNOWN
    UNKNOWN(256);

    private final int type;

    TcpOptionType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }

    public static TcpOptionType getById(int type) {
        for (TcpOptionType value : TcpOptionType.values()) {
            if (value.type == type) return value;
        }

        return UNKNOWN;
    }
}
