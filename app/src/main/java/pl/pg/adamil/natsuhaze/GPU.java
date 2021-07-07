package pl.pg.adamil.natsuhaze;

public class GPU {
    private Screen screen;

    private enum GPUmode {
        HBLANK, VBLANK, OAM, VRAM
    };
    private GPUmode mode;

    private int clock;
    private int line;
    private int scx;
    private int scy;

    private boolean bgmap;

    private byte[] tileset;
    private byte[] oam;
    private byte[] vram;
    private byte[] buffer;
    private byte[] frame;
    private byte[] registers;

    private enum BitValues {
        LCDC, STAT, SCX, SCY, LY, LYC, BGP, OBP0, OBP1, WY, WX
    }

    public GPU(Screen screen) {
        this.screen = screen;
    }

    public void init() {
        System.out.println("Initializing GPU");
        mode = GPUmode.OAM;
        clock = 0;
        line = 0;
        oam = new byte[160];
        vram = new byte[8 * 1024];
        buffer = new byte[8];
        frame = new byte[160 * 144];
        registers = new byte[8];
    }

    public void pushDataToScreen() {
        screen.setData(frame);
    }

    public int getBit(BitValues bitType, int bit) {
        switch(bitType){
            case LCDC:
                return (registers[0x0] >>> bit) & 1;
            default:
                return 0;
        }
    }

    public byte read(int address) {
        return registers[address];
    }

    public void write(int address, byte b) {
        registers[address] = b;
    }

    public void readBgMap() {
        // VRAM offset for the tile map
        int mapOffset = getBit(BitValues.LCDC, 3) == 1 ? 0x1C00 : 0x1800;
        int dataStart = getBit(BitValues.LCDC, 4) == 1 ? 0x0000 : 0x0800;
        // Which line of tiles to use in the map
        int scx = read(0x3);
        int scy = read(0x2);
    }

    public void drawFrame() {

    }

    public byte readVram(int address) {
        return vram[address];
    }

    public void writeVram(int address, byte b) {
        vram[address] = b;
    }

    public byte readOam(int address) {
        return oam[address];
    }

    public void writeOam(int address, byte b) {
        oam[address] = b;
    }

    public void step() {
        switch(mode) {
            case OAM:
                if(clock >= 80)
                {
                    clock = 0;
                    mode = GPUmode.VRAM;
                }
                break;
            case VRAM:
                if(clock >= 172)
                {
                    clock = 0;
                    mode = GPUmode.HBLANK;

                    //readLineToBuffer();
                }
                break;
            case HBLANK:
                if(clock >= 204)
                {
                    clock = 0;
                    line++;

                    if(line >= 143) {
                        mode = GPUmode.VBLANK;
                        drawFrame();
                    } else{
                        mode = GPUmode.HBLANK;
                    }
                }
                break;
            case VBLANK:
                if(clock >= 456)
                {
                    clock = 0;
                    line++;

                    if(line >= 154) {
                        mode = GPUmode.OAM;
                        line = 0;
                    }
                }
                break;
        }
    }
}
