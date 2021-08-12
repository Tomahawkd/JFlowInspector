package io.tomahawkd.jflowinspector.file.protocols.tcp;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.tomahawkd.jflowinspector.file.protocols.tcp.option.*;

import java.util.ArrayList;
import java.util.List;

public class TcpOptionList extends KaitaiStruct {

    private final List<TcpOption> options;

    public TcpOptionList(KaitaiStream _io) {
        super(_io);
        options = new ArrayList<>();
        read();
    }

    private void read() {
        TcpOption option;
        do {
            option = readOption();
            this.options.add(option);
        } while (option.parsedType() != TcpOptionType.EOL && !this._io.isEof());
    }

    private TcpOption readOption() {
        int type = this._io.readU1();
        TcpOptionType parsed = TcpOptionType.getById(type);

        switch (parsed) {
            case EOL: return new EofOption(this._io, parsed);
            case NOP: return new NopOption(this._io, parsed);
            case MSS: return new MSSOption(this._io, parsed);
            case WINDOW_SCALE: return new WindowScaleOption(this._io, parsed);
            case SACK:
            case SACK_PERMIT:
            case TIMESTAMPS:
                return new ContentOption(this._io, parsed);
            default: return new UnknownOption(this._io, type);
        }
    }

    public List<TcpOption> options() {
        return options;
    }

    public <T extends TcpOption> T getOptionByType(Class<T> type) {
        for (TcpOption option : options) {
            if (type.isAssignableFrom(option.getClass())) return (T) option;
        }

        return null;
    }
}
