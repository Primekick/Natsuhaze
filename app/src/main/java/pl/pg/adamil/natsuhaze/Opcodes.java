package pl.pg.adamil.natsuhaze;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Opcodes {
    private HashMap<Byte, TriConsumer<?, ?, ?>> opcode = new HashMap<Byte, TriConsumer<?, ?, ?>>();
    private Memory memory;

    public Opcodes(Memory memory) {
        this.memory = memory;
    }

    public void init() {
        opcode.put((byte) 0x00b, (Void v1, Void v2, Void v3) -> {
            // NOP
        });

        opcode.put((byte) 0x01, (Register16Bit BC, Short nn, Void v) -> {
            // LD BC, u16
            BC.add(nn);
        });

        opcode.put((byte) 0x02, (Register16Bit BC, Short nn, Void v) -> {
            // LD (BC), A
            //memory.
        });

        opcode.put((byte) 0x03, (Register16Bit BC, Void v1, Void v2) -> {
            // INC BC
            BC.inc();
        });

        opcode.put((byte) 0x04, (Register16Bit BC, Void v1, Void v2) -> {
            // INC B
            BC.addHigh((byte) 1);
        });

        opcode.put((byte) 0x05, (Register16Bit BC, Void v1, Void v2) -> {
            // DEC B
            BC.addHigh((byte) 1);
        });

        opcode.put((byte) 0x06, (Register16Bit BC, Byte n, Void v) -> {
            // LD B, u8
            BC.setHigh(n);
        });
    }

    public TriConsumer fetch(byte code) {
        return opcode.get(code);
    }
}
