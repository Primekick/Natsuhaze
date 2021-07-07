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
        AF = new Register16Bit((short) 0x0000);
        BC = new Register16Bit((short) 0x0000);
        DE = new Register16Bit((short) 0x0000);
        HL = new Register16Bit((short) 0x0000);
        SP = new Register16Bit((short) 0xFFFE); // stack pointer start
        PC = new Register16Bit((short) 0x0100); // boot roms start at 0x0000, otherwise 0x1000
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
        byte A = AF.getHigh();
        switch(flag) {
            case ZERO:
                return (A & 128); // 7th bit
            case CARRY:
                return (A & 16); // 4th bit
            default:
                return 0;
        }
    }

    public byte readByte(int address) {
        return memory.readByte(address);
    }

    public short readWord(int address) {
        return memory.readWord(address);
    }

    public void step() {
        byte opcode = memory.readByte(PC.intValue());
        Log.i("Opcode", Integer.toHexString(opcode).substring(0, 2));
        Log.i("PC", Integer.toHexString(PC.intValue()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PC.inc();
        Runnable code = opcodes.fetch(opcode);
        if(code != null)
            code.run();
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
