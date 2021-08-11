package io.tomahawkd.jflowinspector.file.protocols.ether;

import java.util.HashMap;
import java.util.Map;

public enum EtherType {

    IPV4(2048),
    X_75_INTERNET(2049),
    NBS_INTERNET(2050),
    ECMA_INTERNET(2051),
    CHAOSNET(2052),
    X_25_LEVEL_3(2053),
    ARP(2054),
    IEEE_802_1Q_TPID(33024),
    IPV6(34525);

    private final long id;

    EtherType(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    private static final Map<Long, EtherType> byId = new HashMap<>(9);

    static {
        for (EtherType e : EtherType.values())
            byId.put(e.id(), e);
    }

    public static EtherType byId(long id) {
        return byId.get(id);
    }
}
