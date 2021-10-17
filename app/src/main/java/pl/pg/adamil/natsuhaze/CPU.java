package pl.pg.adamil.natsuhaze;

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

    private Register16Bit scx;
    private Register16Bit scy;

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
        setScx(new Register16Bit((short) 0));
        setScy(new Register16Bit((short) 0));
        setAF(new Register16Bit((short) 0x01B0));
        setBC(new Register16Bit((short) 0x0013));
        setDE(new Register16Bit((short) 0x00D8));
        setHL(new Register16Bit((short) 0x014D));
        setSP(new Register16Bit((short) 0xFFFE)); // stack pointer start
        setPC(new Register16Bit((short) 0x0100)); // cartridges boot from 0x0100

        gpu.getScreen().setReg("AF", getAF());
        gpu.getScreen().setReg("BC", getBC());
        gpu.getScreen().setReg("DE", getDE());
        gpu.getScreen().setReg("HL", getHL());
        gpu.getScreen().setReg("SP", getSP());
        gpu.getScreen().setReg("PC", getPC());
        gpu.getScreen().setReg("scx", getScy());
        gpu.getScreen().setReg("scy", getScx());
    }

    public void loadCart(Cartridge cart) {
        this.cart = cart;
        memory.loadFromCart(cart);
    }

    public void writeByte(int address, byte b) {
        memory.writeByte(address, b);
    }

    public int getFlag(Flags flag) {
        byte F = getAF().getLow();
        switch(flag) {
            case ZERO:
                return (F >>> 7) & 1; // 7th bit
            case ADDSUB:
                return (F >>> 6) & 1; // 6th bit
            case HALFCARRY:
                return (F >>> 5) & 1; // 5th bit
            case CARRY:
                return (F >>> 4) & 1; // 4th bit
            default:
                return 0;
        }
    }

    public void setFlag(Flags flag) {
        int F = getAF().getLow();
        switch(flag) {
            case ZERO:
                F |= 1 << 7; // 7th bit
            case ADDSUB:
                F |= 1 << 6; // 6th bit
            case HALFCARRY:
                F |= 1 << 5; // 5th bit
            case CARRY:
                F |= 1 << 4; // 4th bit
        }
        getAF().setLow((byte) (F & 0xFF));
    }

    public void unsetFlag(Flags flag) {
        int F = getAF().getLow();
        switch(flag) {
            case ZERO:
                F &= ~(1 << 7); // 7th bit
            case ADDSUB:
                F &= ~(1 << 6); // 6th bit
            case HALFCARRY:
                F |= ~(1 << 5); // 5th bit
            case CARRY:
                F &= ~(1 << 4); // 4th bit
        }
        getAF().setLow((byte) (F & 0xFF));
    }

    public byte readByte(int address) {
        return memory.readByte(address);
    }

    public short readWord(int address) {
        return memory.readWord(address);
    }

    public void step() {
        byte opcode = memory.readByte(getPC().intValue());
        //Log.i("Opcode", Integer.toHexString(opcode));
        //Log.i("PC", Integer.toHexString(PC.intValue()));
        getPC().inc();
        Runnable code = opcodes.fetch(opcode);
        if(code != null)
            code.run();
        code = null;
        getScx().set((short) gpu.scx);
        getScy().set((short) gpu.scy);
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

    public Register16Bit getAF() {
        return AF;
    }

    public void setAF(Register16Bit AF) {
        this.AF = AF;
    }

    public Register16Bit getBC() {
        return BC;
    }

    public void setBC(Register16Bit BC) {
        this.BC = BC;
    }

    public Register16Bit getDE() {
        return DE;
    }

    public void setDE(Register16Bit DE) {
        this.DE = DE;
    }

    public Register16Bit getHL() {
        return HL;
    }

    public void setHL(Register16Bit HL) {
        this.HL = HL;
    }

    public Register16Bit getSP() {
        return SP;
    }

    public void setSP(Register16Bit SP) {
        this.SP = SP;
    }

    public Register16Bit getPC() {
        return PC;
    }

    public void setPC(Register16Bit PC) {
        this.PC = PC;
    }

    public Register16Bit getScx() {
        return scx;
    }

    public void setScx(Register16Bit scx) {
        this.scx = scx;
    }

    public Register16Bit getScy() {
        return scy;
    }

    public void setScy(Register16Bit scy) {
        this.scy = scy;
    }

}
