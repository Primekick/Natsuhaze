package pl.pg.adamil.natsuhaze;

import android.util.Log;

public class CPU {

    private NatsuhazeCore core;
    private GPU gpu;
    private Cartridge cart;
    private Memory memory;
    private Opcodes opcodes;

    public Register16Bit AF; // Accumulator & Flags
    public Register16Bit BC; // reg B + C
    public Register16Bit DE; // reg D + E
    public Register16Bit HL; // reg H + L
    public Register16Bit SP; // Stack Pointer
    public Register16Bit PC; // Program Counter/Pointer

    public Register16Bit scx;
    public Register16Bit scy;

    public enum Flags {
        ZERO, ADDSUB, HALFCARRY, CARRY
    }

    public CPU(NatsuhazeCore core, GPU gpu) {
        this.core = core;
        this.gpu = gpu;
    }

    public void init() {
        System.out.println("Initializing CPU");
        memory = new Memory(this, gpu);
        opcodes = new Opcodes(this);
        opcodes.init();
        //below are the register values right after executing bootrom
        scx = new Register16Bit((short) 0);
        scy = new Register16Bit((short) 0);
        AF = new Register16Bit((short) 0x01B0);
        BC = new Register16Bit((short) 0x0013);
        DE = new Register16Bit((short) 0x00D8);
        HL = new Register16Bit((short) 0x014D);
        SP = new Register16Bit((short) 0xFFFE); // stack pointer start
        PC = new Register16Bit((short) 0x0100); // cartridges boot from 0x0100
    }

    public void loadCart(Cartridge cart) {
        this.cart = cart;
        memory.loadFromCart(cart);
    }

    public void writeByte(int address, byte b) {
        memory.writeByte(address, b);
    }

    public void pushSP() {

    }

    public int getFlag(Flags flag) {
        byte F = AF.getLow();
        switch(flag) {
            case ZERO:
                return (F >>> 7) & 1; // 7th bit
            case CARRY:
                return (F >>> 4) & 1; // 4th bit
            default:
                return 0;
        }
    }

    public void setFlag(Flags flag) {
        int F = AF.getLow();
        switch(flag) {
            case ZERO:
                F |= 1 << 7; // 7th bit
            case CARRY:
                F |= 1 << 4; // 4th bit
        }
        AF.setLow((byte) (F & 0xFF));
    }

    public void unsetFlag(Flags flag) {
        int F = AF.getLow();
        switch(flag) {
            case ZERO:
                F &= ~(1 << 7); // 7th bit
            case CARRY:
                F &= ~(1 << 4); // 4th bit
        }
        AF.setLow((byte) (F & 0xFF));
    }

    public byte readByte(int address) {
        return memory.readByte(address);
    }

    public short readWord(int address) {
        return memory.readWord(address);
    }

    public void step() {
        byte opcode = memory.readByte(PC.intValue());
        //Log.i("Opcode", Integer.toHexString(opcode));
        //Log.i("PC", Integer.toHexString(PC.intValue()));
        PC.inc();
        Runnable code = opcodes.fetch(opcode);
        if(code != null)
            code.run();
        code = null;
        scx.set((short) gpu.scx);
        scy.set((short) gpu.scy);
        gpu.getScreen().setReg("AF", AF);
        gpu.getScreen().setReg("BC", BC);
        gpu.getScreen().setReg("DE", DE);
        gpu.getScreen().setReg("HL", HL);
        gpu.getScreen().setReg("SP", SP);
        gpu.getScreen().setReg("PC", PC);
        gpu.getScreen().setReg("scx", scy);
        gpu.getScreen().setReg("scy", scx);
    }

    public void requestInterrupt(InterruptType iType) {
        switch(iType) {
            case VBLANK:
                break;
            case HILO:
                break;
            case LCDC:
                break;
            case TIMER:
                break;
            case SERIAL:
                break;
        }
    }

    public String getGameTitle() {
        return cart.getTitle();
    }
}
