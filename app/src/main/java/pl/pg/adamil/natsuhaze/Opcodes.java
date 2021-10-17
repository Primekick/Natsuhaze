package pl.pg.adamil.natsuhaze;

import java.util.HashMap;

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
            cpu.BC.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
            cpu.BC.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x02, () -> {
            // LD (BC), A
            cpu.writeByte(cpu.BC.intValue() & 0xFFFF, cpu.AF.getHigh());
        });

        opcode.put((byte) 0x03, () -> {
            // INC BC
            cpu.BC.inc();
            if ((cpu.BC.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x04, () -> {
            // INC B
            cpu.BC.addHigh((byte) 1);
            if (cpu.BC.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x05, () -> {
            // DEC B
            cpu.BC.subHigh((byte) 1);
            if (cpu.BC.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x06, () -> {
            // LD B, u8
            cpu.BC.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x07, () -> {
            // RLCA
            byte A = cpu.AF.getHigh();
            int msb  = (A >>> 7) & 1;
            A = (byte) (((A << 1) | msb) & 0xff);

            if(msb > 0) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if(A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0x08, () -> {
            // LD (u16), SP
            byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            int address = ((high << 8)  + (low & 0x00FF)) & 0xFFFF;
            cpu.writeByte(address, cpu.SP.getLow());
            address += 1;
            cpu.writeByte(address, cpu.SP.getHigh());
        });

        opcode.put((byte) 0x09, () -> {
            // ADD HL,BC
            cpu.HL.add(cpu.BC.shortValue());
        });

        opcode.put((byte) 0x0A, () -> {
            // LD A,(BC)
            cpu.AF.setHigh(cpu.readByte(cpu.BC.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x0B, () -> {
            // DEC BC
            cpu.BC.dec();
            if ((cpu.BC.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x0C, () -> {
            // INC C
            cpu.BC.addLow((byte) 1);
            if (cpu.BC.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x0D, () -> {
            // DEC C
            cpu.BC.subLow((byte) 1);
            if (cpu.BC.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x0E, () -> {
            // LD C,u8
            cpu.BC.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x0F, () -> {
            // RRCA
            byte A = cpu.AF.getHigh();
            int msb  = A & 0b00000001;
            int newMsb = msb << 7;
            A = (byte) ((A >> 1) & 0b01111111);
            A = (byte) (A | newMsb);

            if(msb > 0) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if(A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0x10, () -> {
            // STOP
            cpu.PC.inc();
        });

        opcode.put((byte) 0x11, () -> {
            // LD DE,u16
            cpu.DE.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
            cpu.DE.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x12, () -> {
            // LD (DE),A
            cpu.writeByte(cpu.DE.intValue() & 0xFFFF & 0xFFFF, cpu.AF.getHigh());
        });

        opcode.put((byte) 0x13, () -> {
            // INC DE
            cpu.DE.inc();
            if ((cpu.DE.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x14, () -> {
            // INC D
            cpu.DE.addHigh((byte) 1);
            if (cpu.DE.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x15, () -> {
            // DEC D
            cpu.DE.subHigh((byte) 1);
            if (cpu.DE.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x16, () -> {
            // LD D, u8
            cpu.DE.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x17, () -> {
            // RLA
            byte A = cpu.AF.getHigh();
            int msb  = (A >>> 7) & 1;
            int carry = cpu.getFlag(CPU.Flags.CARRY);
            A = (byte) (((A << 1) | carry) & 0xff);

            
            if(msb > 0) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if(A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0x18, () -> {
            // JR, i8
            byte i8 = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            cpu.PC.set((short) (cpu.PC.shortValue() + i8));
        });

        opcode.put((byte) 0x19, () -> {
            // ADD HL, DE
            cpu.HL.add(cpu.DE.shortValue());
        });

        opcode.put((byte) 0x1A, () -> {
            // LD A, (DE)
            cpu.AF.setHigh(cpu.readByte(cpu.DE.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x1B, () -> {
            // DEC DE
            cpu.DE.dec();
            if ((cpu.DE.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x1C, () -> {
            // INC E
            cpu.DE.addLow((byte) 1);
            if (cpu.DE.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x1D, () -> {
            // DEC E
            cpu.DE.subLow((byte) 1);
            if (cpu.DE.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x1E, () -> {
            // LD E, u8
            cpu.DE.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x1F, () -> {
            // RRA
            byte A = cpu.AF.getHigh();
            int msb  = A & 0b00000001;
            int carry = cpu.getFlag(CPU.Flags.CARRY);
            carry = (byte) carry << 7;
            A = (byte) ((A >> 1) & 0b01111111);
            A = (byte) (A | carry);

            if(msb > 0) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if(A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0x20, () -> {
            // JR NZ, i8
            if(cpu.getFlag(CPU.Flags.ZERO) > 0) {
                cpu.PC.inc();
            } else {
                byte i8 = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.set((short) (cpu.PC.shortValue() + i8));
            }
        });

        opcode.put((byte) 0x21, () -> {
            // LD HL,u16
            cpu.HL.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
            cpu.HL.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x22, () -> {
            // LD (HL+),A
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.AF.getHigh());
            cpu.HL.inc();
        });

        opcode.put((byte) 0x23, () -> {
            // INC HL
            cpu.HL.inc();
            if ((cpu.HL.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x24, () -> {
            // INC H
            cpu.HL.addHigh((byte) 1);
            if (cpu.HL.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x25, () -> {
            // DEC H
            cpu.HL.subHigh((byte) 1);
            if (cpu.HL.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x26, () -> {
            // LD H, u8
            cpu.HL.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x27, () -> {
            // DAA
            int A = cpu.AF.getHigh();

            

            cpu.AF.setHigh((byte) A);
        });

        opcode.put((byte) 0x28, () -> {
            // JR Z, i8
            if(cpu.getFlag(CPU.Flags.ZERO) == 0) {
                cpu.PC.inc();
            } else {
                byte i8 = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.set((short) (cpu.PC.shortValue() + i8));
            }
        });
        opcode.put((byte) 0x29, () -> {
            // ADD HL, HL
            cpu.HL.add(cpu.HL.shortValue());
        });

        opcode.put((byte) 0x2A, () -> {
            // LD A, (HL+)
            cpu.AF.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
            cpu.HL.inc();
        });

        opcode.put((byte) 0x2B, () -> {
            // DEC HL
            cpu.HL.dec();
            if ((cpu.HL.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x2C, () -> {
            // INC L
            cpu.HL.addLow((byte) 1);
            if (cpu.HL.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x2D, () -> {
            // DEC L
            cpu.HL.subLow((byte) 1);
            if (cpu.HL.getLow() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x2E, () -> {
            // LD L, u8
            cpu.HL.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x2F, () -> {
            // CPL
            cpu.AF.setHigh((byte) (~cpu.AF.getHigh()));
            cpu.setFlag(CPU.Flags.ADDSUB);
            cpu.setFlag(CPU.Flags.HALFCARRY);
        });

        opcode.put((byte) 0x30, () -> {
            // JR NC, i8
            if(cpu.getFlag(CPU.Flags.CARRY) > 0) {
                cpu.PC.inc();
            } else {
                byte i8 = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.set((short) (cpu.PC.shortValue() + i8));
            }
        });

        opcode.put((byte) 0x31, () -> {
            // LD SP,u16
            cpu.SP.setLow(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
            cpu.SP.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x32, () -> {
            // LD (HL-),A
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF & 0xFFFF, cpu.AF.getHigh());
            cpu.HL.dec();
        });

        opcode.put((byte) 0x33, () -> {
            // INC SP
            cpu.SP.inc();
            if ((cpu.SP.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x34, () -> {
            // INC (HL)
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, (byte) (cpu.readByte(cpu.HL.intValue() & 0xFFFF) + 1));
            if (cpu.readByte(cpu.HL.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x35, () -> {
            // DEC (HL)
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, (byte) (cpu.readByte(cpu.HL.intValue() & 0xFFFF) - 1));
            if (cpu.readByte(cpu.HL.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x36, () -> {
            // LD (HL), u8
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x37, () -> {
            // SCF
            cpu.setFlag(CPU.Flags.CARRY);
            cpu.unsetFlag(CPU.Flags.HALFCARRY);
            cpu.unsetFlag(CPU.Flags.ADDSUB);
        });

        opcode.put((byte) 0x38, () -> {
            // JR C, i8
            if(cpu.getFlag(CPU.Flags.CARRY) == 0) {
                cpu.PC.inc();
            } else {
                byte i8 = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.set((short) (cpu.PC.shortValue() + i8));
            }
        });
        opcode.put((byte) 0x39, () -> {
            // ADD HL, SP
            cpu.HL.add(cpu.SP.shortValue());
        });

        opcode.put((byte) 0x3A, () -> {
            // LD A, (HL-)
            cpu.AF.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
            cpu.HL.dec();
        });

        opcode.put((byte) 0x3B, () -> {
            // DEC SP
            cpu.SP.dec();
            if ((cpu.SP.intValue() & 0xFFFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x3C, () -> {
            // INC A
            cpu.AF.addHigh((byte) 1);
            if (cpu.AF.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x3D, () -> {
            // DEC A
            cpu.AF.subHigh((byte) 1);
            if (cpu.AF.getHigh() == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0x3E, () -> {
            // LD A, u8
            cpu.AF.setHigh(cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
        });

        opcode.put((byte) 0x3F, () -> {
            // CCF
            int carry = cpu.getFlag(CPU.Flags.CARRY);
            if(carry == 0) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            cpu.unsetFlag(CPU.Flags.HALFCARRY);
            cpu.unsetFlag(CPU.Flags.ADDSUB);
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
            cpu.BC.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x47, () -> {
            // LD B,A
            cpu.BC.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x48, () -> {
            // LD C,B
            cpu.BC.setLow(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x49, () -> {
            // LD C,C
            cpu.BC.setLow(cpu.BC.getLow());
        });

        opcode.put((byte) 0x4A, () -> {
            // LD C,D
            cpu.BC.setLow(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x4B, () -> {
            // LD C,E
            cpu.BC.setLow(cpu.DE.getLow());
        });

        opcode.put((byte) 0x4C, () -> {
            // LD C,H
            cpu.BC.setLow(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x4D, () -> {
            // LD C,L
            cpu.BC.setLow(cpu.HL.getLow());
        });

        opcode.put((byte) 0x4E, () -> {
            // LD C,(HL)
            cpu.BC.setLow(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x4F, () -> {
            // LD C,A
            cpu.BC.setLow(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x50, () -> {
            // LD D,B
            cpu.DE.setHigh(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x51, () -> {
            // LD D,C
            cpu.DE.setHigh(cpu.BC.getLow());
        });

        opcode.put((byte) 0x52, () -> {
            // LD D,D
            cpu.DE.setHigh(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x53, () -> {
            // LD D,E
            cpu.DE.setHigh(cpu.DE.getLow());
        });

        opcode.put((byte) 0x54, () -> {
            // LD D,H
            cpu.DE.setHigh(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x55, () -> {
            // LD D,L
            cpu.DE.setHigh(cpu.HL.getLow());
        });

        opcode.put((byte) 0x56, () -> {
            // LD D,(HL)
            cpu.DE.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x57, () -> {
            // LD D,A
            cpu.DE.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x58, () -> {
            // LD E,B
            cpu.DE.setLow(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x59, () -> {
            // LD E,C
            cpu.DE.setLow(cpu.BC.getLow());
        });

        opcode.put((byte) 0x5A, () -> {
            // LD E,D
            cpu.DE.setLow(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x5B, () -> {
            // LD E,E
            cpu.DE.setLow(cpu.DE.getLow());
        });

        opcode.put((byte) 0x5C, () -> {
            // LD E,H
            cpu.DE.setLow(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x5D, () -> {
            // LD E,L
            cpu.DE.setLow(cpu.HL.getLow());
        });

        opcode.put((byte) 0x5E, () -> {
            // LD E,(HL)
            cpu.DE.setLow(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x5F, () -> {
            // LD E,A
            cpu.DE.setLow(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x60, () -> {
            // LD H,B
            cpu.HL.setHigh(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x61, () -> {
            // LD H,C
            cpu.HL.setHigh(cpu.BC.getLow());
        });

        opcode.put((byte) 0x62, () -> {
            // LD H,D
            cpu.HL.setHigh(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x63, () -> {
            // LD H,E
            cpu.HL.setHigh(cpu.DE.getLow());
        });

        opcode.put((byte) 0x64, () -> {
            // LD H,H
            cpu.HL.setHigh(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x65, () -> {
            // LD H,L
            cpu.HL.setHigh(cpu.HL.getLow());
        });

        opcode.put((byte) 0x66, () -> {
            // LD H,(HL)
            cpu.HL.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x67, () -> {
            // LD H,A
            cpu.HL.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x68, () -> {
            // LD L,B
            cpu.HL.setLow(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x69, () -> {
            // LD L,C
            cpu.HL.setLow(cpu.BC.getLow());
        });

        opcode.put((byte) 0x6A, () -> {
            // LD L,D
            cpu.HL.setLow(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x6B, () -> {
            // LD L,E
            cpu.HL.setLow(cpu.DE.getLow());
        });

        opcode.put((byte) 0x6C, () -> {
            // LD L,H
            cpu.HL.setLow(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x6D, () -> {
            // LD L,L
            cpu.HL.setLow(cpu.HL.getLow());
        });

        opcode.put((byte) 0x6E, () -> {
            // LD L,(HL)
            cpu.HL.setLow(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x6F, () -> {
            // LD L,A
            cpu.HL.setLow(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x70, () -> {
            // LD (HL),B
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.BC.getHigh());
        });

        opcode.put((byte) 0x71, () -> {
            // LD (HL),C
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.BC.getLow());
        });

        opcode.put((byte) 0x72, () -> {
            // LD (HL),D
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.DE.getHigh());
        });

        opcode.put((byte) 0x73, () -> {
            // LD (HL),E
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.DE.getLow());
        });

        opcode.put((byte) 0x74, () -> {
            // LD (HL),H
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.HL.getHigh());
        });

        opcode.put((byte) 0x75, () -> {
            // LD (HL),L
            cpu.writeByte(cpu.HL.intValue() & 0xFFFF, cpu.HL.getLow());
        });

        opcode.put((byte) 0x76, () -> {
            // HALT
            // TODO - two nop???
        });

        opcode.put((byte) 0x77, () -> {
            // LD (HL),A
            cpu.HL.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x78, () -> {
            // LD A,B
            cpu.AF.setHigh(cpu.BC.getHigh());
        });

        opcode.put((byte) 0x79, () -> {
            // LD A,C
            cpu.AF.setHigh(cpu.BC.getLow());
        });

        opcode.put((byte) 0x7A, () -> {
            // LD A,D
            cpu.AF.setHigh(cpu.DE.getHigh());
        });

        opcode.put((byte) 0x7B, () -> {
            // LD A,E
            cpu.AF.setHigh(cpu.DE.getLow());
        });

        opcode.put((byte) 0x7C, () -> {
            // LD A,H
            cpu.AF.setHigh(cpu.HL.getHigh());
        });

        opcode.put((byte) 0x7D, () -> {
            // LD A,L
            cpu.AF.setHigh(cpu.HL.getLow());
        });

        opcode.put((byte) 0x7E, () -> {
            // LD A,(HL)
            cpu.AF.setHigh(cpu.readByte(cpu.HL.intValue() & 0xFFFF));
        });

        opcode.put((byte) 0x7F, () -> {
            // LD A,A
            cpu.AF.setHigh(cpu.AF.getHigh());
        });

        opcode.put((byte) 0x80, () -> {
            // ADD A,B
            int A = cpu.AF.getHigh() + cpu.BC.getHigh();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x81, () -> {
            // ADD A,C
            int A = cpu.AF.getHigh() + cpu.BC.getLow();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x82, () -> {
            // ADD A,D
            int A = cpu.AF.getHigh() + cpu.DE.getHigh();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x83, () -> {
            // ADD A,E
            int A = cpu.AF.getHigh() + cpu.DE.getLow();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x84, () -> {
            // ADD A,H
            int A = cpu.AF.getHigh() + cpu.HL.getHigh();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x85, () -> {
            // ADD A,L
            int A = cpu.AF.getHigh() + cpu.HL.getLow();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x86, () -> {
            // ADD A,(HL)
            int A = cpu.AF.getHigh() + cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x87, () -> {
            // ADD A,A
            int A = cpu.AF.getHigh() + cpu.AF.getHigh();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x88, () -> {
            // ADC A,B
            int A = cpu.AF.getHigh() + cpu.BC.getHigh() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x89, () -> {
            // ADC A,C
            int A = cpu.AF.getHigh() + cpu.BC.getLow() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8A, () -> {
            // ADC A,D
            int A = cpu.AF.getHigh() + cpu.DE.getHigh() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8B, () -> {
            // ADC A,E
            int A = cpu.AF.getHigh() + cpu.DE.getLow() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8C, () -> {
            // ADC A,H
            int A = cpu.AF.getHigh() + cpu.HL.getHigh() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8D, () -> {
            // ADC A,L
            int A = cpu.AF.getHigh() + cpu.HL.getLow() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8E, () -> {
            // ADC A,(HL)
            int A = cpu.AF.getHigh() + cpu.readByte(cpu.HL.intValue() & 0xFFFF) + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x8F, () -> {
            // ADC A,A
            int A = cpu.AF.getHigh() + cpu.AF.getHigh() + cpu.getFlag(CPU.Flags.CARRY);
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x90, () -> {
            // SUB A,B
            int A = cpu.AF.getHigh() - cpu.BC.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x91, () -> {
            // SUB A,C
            int A = cpu.AF.getHigh() - cpu.BC.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x92, () -> {
            // SUB A,D
            int A = cpu.AF.getHigh() - cpu.DE.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x93, () -> {
            // SUB A,E
            int A = cpu.AF.getHigh() - cpu.DE.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x94, () -> {
            // SUB A,H
            int A = cpu.AF.getHigh() - cpu.HL.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x95, () -> {
            // SUB A,L
            int A = cpu.AF.getHigh() - cpu.HL.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x96, () -> {
            // SUB A,(HL)
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x97, () -> {
            // SUB A,A
            int A = cpu.AF.getHigh() - cpu.AF.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x98, () -> {
            // SBC A,B
            int A = cpu.AF.getHigh() - cpu.BC.getHigh() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x99, () -> {
            // SBC A,C
            int A = cpu.AF.getHigh() - cpu.BC.getLow() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9A, () -> {
            // SBC A,D
            int A = cpu.AF.getHigh() - cpu.DE.getHigh() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9B, () -> {
            // SBC A,E
            int A = cpu.AF.getHigh() - cpu.DE.getLow() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9C, () -> {
            // SBC A,H
            int A = cpu.AF.getHigh() - cpu.HL.getHigh() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9D, () -> {
            // SBC A,L
            int A = cpu.AF.getHigh() - cpu.HL.getLow() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9E, () -> {
            // SBC A,(HL)
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.HL.intValue() & 0xFFFF) - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0x9F, () -> {
            // SBC A,A
            int A = cpu.AF.getHigh() - cpu.AF.getHigh() - cpu.getFlag(CPU.Flags.CARRY);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA0, () -> {
            // AND A,B
            int A = cpu.AF.getHigh() & cpu.BC.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA1, () -> {
            // AND A,C
            int A = cpu.AF.getHigh() & cpu.BC.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA2, () -> {
            // AND A,D
            int A = cpu.AF.getHigh() & cpu.DE.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA3, () -> {
            // AND A,E
            int A = cpu.AF.getHigh() & cpu.DE.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA4, () -> {
            // AND A,H
            int A = cpu.AF.getHigh() & cpu.HL.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA5, () -> {
            // AND A,L
            int A = cpu.AF.getHigh() & cpu.HL.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA6, () -> {
            // AND A,(HL)
            int A = cpu.AF.getHigh() & cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA7, () -> {
            // AND A,A
            int A = cpu.AF.getHigh() & cpu.AF.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA8, () -> {
            // XOR A,B
            int A = cpu.AF.getHigh() ^ cpu.BC.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xA9, () -> {
            // XOR A,C
            int A = cpu.AF.getHigh() ^ cpu.BC.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAA, () -> {
            // XOR A,D
            int A = cpu.AF.getHigh() ^ cpu.DE.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAB, () -> {
            // XOR A,E
            int A = cpu.AF.getHigh() ^ cpu.DE.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAC, () -> {
            // XOR A,H
            int A = cpu.AF.getHigh() ^ cpu.HL.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAD, () -> {
            // XOR A,L
            int A = cpu.AF.getHigh() ^ cpu.HL.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAE, () -> {
            // XOR A,(HL)
            int A = cpu.AF.getHigh() ^ cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xAF, () -> {
            // XOR A,A
            int A = cpu.AF.getHigh() ^ cpu.AF.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB0, () -> {
            // OR A,B
            int A = cpu.AF.getHigh() | cpu.BC.getHigh();
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB1, () -> {
            // OR A,C
            int A = cpu.AF.getHigh() | cpu.BC.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB2, () -> {
            // OR A,D
            int A = cpu.AF.getHigh() | cpu.DE.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB3, () -> {
            // OR A,E
            int A = cpu.AF.getHigh() | cpu.DE.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB4, () -> {
            // OR A,H
            int A = cpu.AF.getHigh() | cpu.HL.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB5, () -> {
            // OR A,L
            int A = cpu.AF.getHigh() | cpu.HL.getLow();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB6, () -> {
            // OR A,(HL)
            int A = cpu.AF.getHigh() | cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB7, () -> {
            // OR A,A
            int A = cpu.AF.getHigh() | cpu.AF.getHigh();
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xB8, () -> {
            // CP A,B
            int A = cpu.AF.getHigh() - cpu.BC.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xB9, () -> {
            // CP A,C
            int A = cpu.AF.getHigh() - cpu.BC.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBA, () -> {
            // CP A,D
            int A = cpu.AF.getHigh() - cpu.DE.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBB, () -> {
            // CP A,E
            int A = cpu.AF.getHigh() - cpu.DE.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBC, () -> {
            // CP A,H
            int A = cpu.AF.getHigh() - cpu.HL.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBD, () -> {
            // CP A,L
            int A = cpu.AF.getHigh() - cpu.HL.getLow();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBE, () -> {
            // CP A,(HL)
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.HL.intValue() & 0xFFFF);
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xBF, () -> {
            // CP A,A
            int A = cpu.AF.getHigh() - cpu.AF.getHigh();
            
            if (A < 0) { // underflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            
        });

        opcode.put((byte) 0xC0, () -> {
            // RET NZ
            if(cpu.getFlag(CPU.Flags.ZERO) == 0) {
                byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                cpu.PC.setLow(low);
                cpu.PC.setHigh(high);
            }
        });

        opcode.put((byte) 0xC1, () -> {
            // POP BC
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.BC.setLow(low);
            cpu.BC.setHigh(high);
        });

        opcode.put((byte) 0xC2, () -> {
            // JP NZ,u16
            if(cpu.getFlag(CPU.Flags.ZERO) > 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xC3, () -> {
            // JP u16
            byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            cpu.PC.setHigh(high);
            cpu.PC.setLow(low);
        });

        opcode.put((byte) 0xC4, () -> {
            // CALL NZ, u16
            if(cpu.getFlag(CPU.Flags.ZERO) > 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();

                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xC5, () -> {
            // PUSH BC
            byte low = cpu.BC.getLow();
            byte high = cpu.BC.getHigh();
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, high);
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, low);
        });

        opcode.put((byte) 0xC6, () -> {
            // ADD A,u8
            int A = cpu.AF.getHigh() + cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xC7, () -> {
            // RST 00h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x00);
        });

        opcode.put((byte) 0xC8, () -> {
            // RET Z
            if(cpu.getFlag(CPU.Flags.ZERO) > 0) {
                byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                cpu.PC.setLow(low);
                cpu.PC.setHigh(high);
            }
        });

        opcode.put((byte) 0xC9, () -> {
            // RET
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.PC.setLow(low);
            cpu.PC.setHigh(high);
        });

        opcode.put((byte) 0xCA, () -> {
            // JP Z,u16
            if(cpu.getFlag(CPU.Flags.ZERO) == 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xCB, () -> {
            // PREFIX CB
            // TODO
        });

        opcode.put((byte) 0xCC, () -> {
            // CALL Z, u16
            if(cpu.getFlag(CPU.Flags.ZERO) == 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();

                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xCD, () -> {
            // CALL u16
            byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();

            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh(high);
            cpu.PC.setLow(low);
        });

        opcode.put((byte) 0xCE, () -> {
            // ADC A,u8
            int A = cpu.AF.getHigh() + cpu.readByte(cpu.PC.intValue() & 0xFFFF) + cpu.getFlag(CPU.Flags.CARRY);
            cpu.PC.inc();
            
            if (A != (A & 0xFF)) { // overflow
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xCF, () -> {
            // RST 08h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x08);
        });

        opcode.put((byte) 0xD0, () -> {
            // RET NC
            if(cpu.getFlag(CPU.Flags.CARRY) == 0) {
                byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                cpu.PC.setLow(low);
                cpu.PC.setHigh(high);
            }
        });

        opcode.put((byte) 0xD1, () -> {
            // POP DE
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.DE.setLow(low);
            cpu.DE.setHigh(high);
        });

        opcode.put((byte) 0xD2, () -> {
            // JP NC,u16
            if(cpu.getFlag(CPU.Flags.CARRY) > 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xD3, () -> {
            // empty
        });

        opcode.put((byte) 0xD4, () -> {
            // CALL NC,u16
            if(cpu.getFlag(CPU.Flags.CARRY) > 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();

                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xD5, () -> {
            // PUSH DE
            byte low = cpu.DE.getLow();
            byte high = cpu.DE.getHigh();
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, high);
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, low);
        });

        opcode.put((byte) 0xD6, () -> {
            // SUB A,u8
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            
            if (A != (A & 0xFF)) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xD7, () -> {
            // RST 10h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x10);
        });

        opcode.put((byte) 0xD8, () -> {
            // RET C
            if(cpu.getFlag(CPU.Flags.CARRY) > 0) {
                byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
                cpu.SP.inc();
                cpu.PC.setLow(low);
                cpu.PC.setHigh(high);
            }
        });

        opcode.put((byte) 0xD9, () -> {
            // RETI
            // RET + enable interrupts - TODO
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.PC.setLow(low);
            cpu.PC.setHigh(high);
        });

        opcode.put((byte) 0xDA, () -> {
            // JP C,u16
            if(cpu.getFlag(CPU.Flags.CARRY) == 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xDB, () -> {
            // empty
        });

        opcode.put((byte) 0xDC, () -> {
            // CALL C,u16
            if(cpu.getFlag(CPU.Flags.CARRY) == 0){
                cpu.PC.inc();
                cpu.PC.inc();
            } else {
                byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();
                byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
                cpu.PC.inc();

                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
                cpu.SP.dec();
                cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

                cpu.PC.setHigh(high);
                cpu.PC.setLow(low);
            }
        });

        opcode.put((byte) 0xDD, () -> {
            // empty
        });

        opcode.put((byte) 0xDE, () -> {
            // SBC A,u8
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.PC.intValue() & 0xFFFF) - cpu.getFlag(CPU.Flags.CARRY);
            cpu.PC.inc();
            
            if (A != (A & 0xFF)) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xDF, () -> {
            // RST 18h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x18);
        });

        opcode.put((byte) 0xE0, () -> {
            // LD (FF00+u8),A
            int address = 0xFF00 + cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            cpu.writeByte(address, cpu.AF.getHigh());
        });

        opcode.put((byte) 0xE1, () -> {
            // POP HL
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.HL.setLow(low);
            cpu.HL.setHigh(high);
        });

        opcode.put((byte) 0xE2, () -> {
            // LD (FF00+C),A
            int address = 0xFF00 + cpu.PC.getLow();
            cpu.writeByte(address, cpu.AF.getHigh());
        });

        opcode.put((byte) 0xE3, () -> {
            // empty
        });

        opcode.put((byte) 0xE4, () -> {
            // empty
        });

        opcode.put((byte) 0xE5, () -> {
            // PUSH HL
            byte low = cpu.HL.getLow();
            byte high = cpu.HL.getHigh();
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, high);
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, low);
        });

        opcode.put((byte) 0xE6, () -> {
            // AND A,u8
            int A = cpu.AF.getHigh() & cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xE7, () -> {
            // RST 20h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x20);
        });

        opcode.put((byte) 0xE8, () -> {
            // ADD SP, i8
            cpu.SP.add((short) (cpu.readByte(cpu.PC.intValue() & 0xFFFF) & 0x00FF));
            cpu.PC.inc();

            cpu.unsetFlag(CPU.Flags.ZERO);
            
        });

        opcode.put((byte) 0xE9, () -> {
            // JP HL
            cpu.PC.setHigh(cpu.HL.getHigh());
            cpu.PC.setLow(cpu.HL.getLow());
        });

        opcode.put((byte) 0xEA, () -> {
            // LD (u16), A
            byte low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            byte high = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            int address = ((high << 8)  + (low & 0x00FF)) & 0xFFFF;
            cpu.writeByte(address, cpu.AF.getHigh());
        });

        opcode.put((byte) 0xEB, () -> {
            // empty
        });

        opcode.put((byte) 0xEC, () -> {
            // empty
        });

        opcode.put((byte) 0xED, () -> {
            // empty
        });

        opcode.put((byte) 0xEE, () -> {
            // XOR A,u8
            int A = cpu.AF.getHigh() ^ cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xEF, () -> {
            // RST 28h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x28);
        });

        opcode.put((byte) 0xF0, () -> {
            // LD A,(FF00+u8)
            int address = 0xFF00 + (cpu.readByte(cpu.PC.intValue() & 0xFFFF) & 0xFF);
            byte A = cpu.readByte(address);
            cpu.PC.inc();
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0xF1, () -> {
            // POP AF
            byte low = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            byte high = cpu.readByte(cpu.SP.intValue() & 0xFFFF);
            cpu.SP.inc();
            cpu.AF.setLow(low);
            cpu.AF.setHigh(high);
        });

        opcode.put((byte) 0xF2, () -> {
            // LD A,(FF00+C)
            byte A = cpu.readByte(0xFF00 + cpu.BC.getLow() & 0xFF);
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0xF3, () -> {
            // DI
            // disable interrupts - TODO
        });

        opcode.put((byte) 0xF4, () -> {
            // empty
        });

        opcode.put((byte) 0xF5, () -> {
            // PUSH AF
            byte low = cpu.AF.getLow();
            byte high = cpu.AF.getHigh();
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, high);
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, low);
        });

        opcode.put((byte) 0xF6, () -> {
            // OR A,u8
            int A = cpu.AF.getHigh() | cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            
            if (A == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
            cpu.unsetFlag(CPU.Flags.CARRY);
            cpu.AF.setHigh((byte) (A & 0xFF));
        });

        opcode.put((byte) 0xF7, () -> {
            // RST 30h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x30);
        });

        opcode.put((byte) 0xF8, () -> {
            // LD HL,SP+i8
            short SP = (short) (cpu.SP.shortValue() + cpu.readByte(cpu.PC.intValue() & 0xFFFF));
            cpu.PC.inc();
            cpu.HL.set(SP);

            cpu.unsetFlag(CPU.Flags.ZERO);
        });

        opcode.put((byte) 0xF9, () -> {
            // LD SP, HL
            cpu.SP.set(cpu.HL.shortValue());
        });

        opcode.put((byte) 0xFA, () -> {
            //LD A,(u16)
            int low = cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            int high = cpu.readByte(cpu.PC.intValue() & 0xFFFF) << 8;
            cpu.PC.inc();
            byte A = cpu.readByte((high + low) & 0xFFFF);
            cpu.AF.setHigh(A);
        });

        opcode.put((byte) 0xFB, () -> {
            // EI
            // enable interrupts - TODO
        });

        opcode.put((byte) 0xFC, () -> {
            // empty
        });

        opcode.put((byte) 0xFD, () -> {
            // empty
        });

        opcode.put((byte) 0xFE, () -> {
            // CP A,u8
            int A = cpu.AF.getHigh() - cpu.readByte(cpu.PC.intValue() & 0xFFFF);
            cpu.PC.inc();
            
            if (A != (A & 0xFF)) {
                cpu.setFlag(CPU.Flags.CARRY);
            } else {
                cpu.unsetFlag(CPU.Flags.CARRY);
            }
            if ((A & 0xFF) == 0) {
                cpu.setFlag(CPU.Flags.ZERO);
            } else {
                cpu.unsetFlag(CPU.Flags.ZERO);
            }
        });

        opcode.put((byte) 0xFF, () -> {
            // RST 38h
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getHigh());
            cpu.SP.dec();
            cpu.writeByte(cpu.SP.intValue() & 0xFFFF, cpu.PC.getLow());

            cpu.PC.setHigh((byte) 0x00);
            cpu.PC.setLow((byte) 0x38);
        });
    }

    public Runnable fetch(byte code) {
        return opcode.get(code);
    }
}
