package pl.pg.adamil.natsuhaze;

public class CPU {

    private NatsuhazeCore core;
    private GPU gpu;

    private Register16Bit AF;
    private Register16Bit BC;
    private Register16Bit DE;
    private Register16Bit HL;
    private Register16Bit SP;
    private Register16Bit PC;

    public CPU(NatsuhazeCore core, GPU gpu) {
        this.core = core;
        this.gpu = gpu;
    }

    public void init() {
    }
}
