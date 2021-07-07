package pl.pg.adamil.natsuhaze;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Opcodes {
    private HashMap<Byte, Runnable> opcode = new HashMap<Byte, Runnable>();
    private CPU cpu;

    public Opcodes(CPU cpu) {
        this.cpu = cpu;
    }

    public void init() {
        opcode.put((byte) 0x00, () -> {
            // NOP
        });

        opcode.put((byte) 0x01, () -> {
            // LD BC, u16
            cpu.BC.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
            cpu.BC.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x02, () -> {
            // LD (BC), A
            cpu.writeByte(cpu.BC.intValue(), cpu.AF.getHigh());
        });

        opcode.put((byte) 0x03, () -> {
            // INC BC
            cpu.BC.inc();
        });

        opcode.put((byte) 0x04, () -> {
            // INC B
            cpu.BC.addHigh((byte) 1);
        });

        opcode.put((byte) 0x05, () -> {
            // DEC B
            cpu.BC.subHigh((byte) 1);
        });

        opcode.put((byte) 0x06, () -> {
            // LD B, u8
            cpu.BC.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x07, () -> {
            // RLCA
        });

        opcode.put((byte) 0x08, () -> {
            // LD (u16), SP
            byte low = cpu.readByte(cpu.PC.intValue());
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue());
            cpu.PC.inc();
            short word = (short) ((short) (high << 8) + (short)(low & 0x00FF));
            cpu.writeByte(word, cpu.SP.getLow());
            word = (short) (word + 1);
            cpu.writeByte(word, cpu.SP.getHigh());
        });

        opcode.put((byte) 0x09, () -> {
            // ADD HL,BC
            cpu.HL.add(cpu.BC.shortValue());
        });

        opcode.put((byte) 0x0A, () -> {
            // LD A,(BC)
            cpu.AF.setHigh(cpu.readByte(cpu.BC.intValue()));
        });

        opcode.put((byte) 0x0B, () -> {
            // DEC BC
            cpu.BC.dec();
        });

        opcode.put((byte) 0x0C, () -> {
            // INC C
            cpu.BC.addLow((byte) 1);
        });

        opcode.put((byte) 0x0D, () -> {
            // DEC C
            cpu.BC.subLow((byte) 1);
        });

        opcode.put((byte) 0x0E, () -> {
            // LD C,u8
            cpu.BC.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x0F, () -> {
            // RRCA
        });

        opcode.put((byte) 0x10, () -> {
            // STOP
        });

        opcode.put((byte) 0x11, () -> {
            // LD DE,u16
            cpu.DE.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
            cpu.DE.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x12, () -> {
            // LD (DE),A
        });

        opcode.put((byte) 0x13, () -> {
            // INC DE
            cpu.DE.inc();
        });

        opcode.put((byte) 0x14, () -> {
            // INC D
            cpu.DE.addHigh((byte) 1);
        });

        opcode.put((byte) 0x15, () -> {
            // DEC D
            cpu.DE.subHigh((byte) 1);
        });

        opcode.put((byte) 0x16, () -> {
            // LD D, u8
            cpu.DE.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x17, () -> {
            // RRCA
        });

        opcode.put((byte) 0x18, () -> {
            // JR, i8

        });

        opcode.put((byte) 0x19, () -> {
            // ADD HL, DE
            cpu.HL.add(cpu.DE.shortValue());
        });

        opcode.put((byte) 0x1A, () -> {
            // LD A, (DE)
            cpu.AF.setHigh(cpu.readByte(cpu.DE.intValue()));
        });

        opcode.put((byte) 0x1B, () -> {
            // DEC DE
            cpu.DE.dec();
        });

        opcode.put((byte) 0x1C, () -> {
            // INC E
            cpu.DE.addLow((byte) 1);
        });

        opcode.put((byte) 0x1D, () -> {
            // DEC E
            cpu.DE.subLow((byte) 1);
        });

        opcode.put((byte) 0x1E, () -> {
            // LD E, u8
            cpu.DE.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x1F, () -> {
            // RRA

        });

        opcode.put((byte) 0x20, () -> {
            // JR NZ, i8

        });

        opcode.put((byte) 0x21, () -> {
            // LD HL,u16
            cpu.HL.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
            cpu.HL.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x22, () -> {
            // LD (HL+),A
        });

        opcode.put((byte) 0x23, () -> {
            // INC HL
            cpu.HL.inc();
        });

        opcode.put((byte) 0x24, () -> {
            // INC H
            cpu.HL.addHigh((byte) 1);
        });

        opcode.put((byte) 0x25, () -> {
            // DEC H
            cpu.HL.subHigh((byte) 1);
        });

        opcode.put((byte) 0x26, () -> {
            // LD H, u8
            cpu.HL.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x27, () -> {
            // DAA
        });

        opcode.put((byte) 0x28, () -> {
            // JR Z, i8

        });
        opcode.put((byte) 0x29, () -> {
            // ADD HL, HL
            cpu.HL.add(cpu.HL.shortValue());
        });

        opcode.put((byte) 0x2A, () -> {
            // LD A, (HL+)
            cpu.AF.setHigh(cpu.readByte(cpu.HL.intValue()));
            cpu.HL.inc();
        });

        opcode.put((byte) 0x2B, () -> {
            // DEC HL
            cpu.HL.dec();
        });

        opcode.put((byte) 0x2C, () -> {
            // INC L
            cpu.HL.addLow((byte) 1);
        });

        opcode.put((byte) 0x2D, () -> {
            // DEC L
            cpu.HL.subLow((byte) 1);
        });

        opcode.put((byte) 0x2E, () -> {
            // LD L, u8
            cpu.HL.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x2F, () -> {
            // CPL

        });

        opcode.put((byte) 0x30, () -> {
            // JR NC, i8

        });

        opcode.put((byte) 0x31, () -> {
            // LD SP,u16
            cpu.SP.setLow(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
            cpu.SP.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x32, () -> {
            // LD (HL-),A
        });

        opcode.put((byte) 0x33, () -> {
            // INC SP
            cpu.SP.inc();
        });

        opcode.put((byte) 0x34, () -> {
            // INC (HL)
            cpu.writeByte(cpu.HL.intValue(), (byte) (cpu.readByte(cpu.HL.intValue()) + 1));
        });

        opcode.put((byte) 0x35, () -> {
            // DEC (HL)
            cpu.writeByte(cpu.HL.intValue(), (byte) (cpu.readByte(cpu.HL.intValue()) - 1));
        });

        opcode.put((byte) 0x26, () -> {
            // LD (HL), u8
            cpu.writeByte(cpu.HL.intValue(), cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x37, () -> {
            // SCF
        });

        opcode.put((byte) 0x38, () -> {
            // JR C, i8

        });
        opcode.put((byte) 0x39, () -> {
            // ADD HL, SP
            cpu.HL.add(cpu.SP.shortValue());
        });

        opcode.put((byte) 0x3A, () -> {
            // LD A, (HL-)
            cpu.AF.setHigh(cpu.readByte(cpu.HL.intValue()));
            cpu.HL.dec();
        });

        opcode.put((byte) 0x3B, () -> {
            // DEC SP
            cpu.SP.dec();
        });

        opcode.put((byte) 0x3C, () -> {
            // INC A
            cpu.AF.addLow((byte) 1);
        });

        opcode.put((byte) 0x3D, () -> {
            // DEC A
            cpu.AF.subLow((byte) 1);
        });

        opcode.put((byte) 0x3E, () -> {
            // LD A, u8
            cpu.AF.setHigh(cpu.readByte(cpu.PC.intValue()));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x3F, () -> {
            // CCF

        });

        opcode.put((byte) 0x40, () -> {
            // LD B,B
            cpu.BC.setHigh(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x41, () -> {
            // LD B,C
            cpu.BC.setHigh(cpu.BC.getLow());
        });

        opcode.put((byte) 0x42, () -> {
            // LD B,D
            cpu.BC.setHigh(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x43, () -> {
            // LD B,E
            cpu.BC.setHigh(cpu.DE.getLow());
        });

        opcode.put((byte) 0x44, () -> {
            // LD B,H
            cpu.BC.setHigh(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x45, () -> {
            // LD B,L
            cpu.BC.setHigh(cpu.HL.getLow());
        });

        opcode.put((byte) 0x46, () -> {
            // LD B,(HL)
            cpu.BC.setHigh(cpu.readByte(cpu.HL.intValue()));
        });

        opcode.put((byte) 0x47, () -> {
            // LD B,A
            cpu.BC.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x48, () -> {
            // LD C,B

        });

        opcode.put((byte) 0x49, () -> {
            // LD C,C

        });

        opcode.put((byte) 0x4A, () -> {
            // LD C,D

        });

        opcode.put((byte) 0x4B, () -> {
            // LD C,E

        });

        opcode.put((byte) 0x4C, () -> {
            // LD C,H

        });

        opcode.put((byte) 0x4D, () -> {
            // LD C,L

        });

        opcode.put((byte) 0x4E, () -> {
            // LD C,(HL)

        });

        opcode.put((byte) 0x4F, () -> {
            // LD C,A

        });

        opcode.put((byte) 0xC2, () -> {
            // JP NZ,nn
            if(cpu.getFlag(CPU.Flags.ZERO) > 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue());
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue());
                cpu.PC.inc();
                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xC3, () -> {
            // JP nn
            byte low = cpu.readByte(cpu.PC.intValue());
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue());
            cpu.PC.setHigh(high);
            cpu.PC.setLow(low);
        });

        opcode.put((byte) 0xC5, () -> {
            // PUSH BC
        });

        opcode.put((byte) 0xD5, () -> {
            // PUSH DE
        });

        opcode.put((byte) 0xE5, () -> {
            // PUSH HL
        });

        opcode.put((byte) 0xF5, () -> {
            // PUSH AF
        });
    }

    public Runnable fetch(byte code) {
        return opcode.get(code);
    }
}