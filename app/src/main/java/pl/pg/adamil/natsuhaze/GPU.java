package pl.pg.adamil.natsuhaze;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.math.*;

public class GPU {
    private Screen screen;

    private enum GPUmode {
        HBLANK, VBLANK, OAM, VRAM
    };
    private GPUmode mode;

    private int clock;
    private int line;
    public int scx;
    public int scy;

    private int controlRegister;
    private int statusRegister;
    private int dmaRegister;

    private boolean bgmap;

    private Tileset tileset0;
    private Tileset tileset1;
    private TileMap tilemap0;
    private TileMap tilemap1;
    private byte[] oam;
    private byte[] vram;
    private byte[] buffer;
    private byte[] tileBuffer;
    private byte[][] frame;

    private enum BitValues {
        BG, SPRITES, SPRITE_SIZE, TILEMAP, TILESET, WINDOW, WINDOW_TILEMAP, DISPLAY
    }

    public GPU(Screen screen) {
        this.screen = screen;
    }

    public void init() {
        System.out.println("Initializing GPU");
        mode = GPUmode.OAM;
        clock = 0;
        line = 0;

        controlRegister = 0x91;
        statusRegister = 0;
        dmaRegister = 0;
        tileBuffer = new byte[16];
        oam = new byte[160];
        vram = new byte[8 * 1024];
        buffer = new byte[160];
        frame = new byte[144][160];
        tileset0 = new Tileset(this, vram, false);
        tileset1 = new Tileset(this, vram, true);
        tilemap0 = new TileMap(false);
        tilemap1 = new TileMap(true);
    }

    public void pushDataToScreen() {
        screen.setData(frame);
    }

    public int getBitFromControlRegister(BitValues bitType) {
        switch(bitType){
            case BG:
                return controlRegister & 1;
            case SPRITES:
                return (controlRegister >>> 1) & 1;
            case SPRITE_SIZE:
                return (controlRegister >>> 2) & 1;
            case TILEMAP:
                return (controlRegister >>> 3) & 1;
            case TILESET:
                return (controlRegister >>> 4) & 1;
            case WINDOW:
                return (controlRegister >>> 5) & 1;
            case WINDOW_TILEMAP:
                return (controlRegister >>> 6) & 1;
            case DISPLAY:
                return (controlRegister >>> 7) & 1;
            default:
                return -1;
        }
    }

    public byte read(int address) {
        switch(address){
            case 0x00: // LCD + GPU control
                return (byte) (controlRegister & 0xFF);
            case 0x01: // LCD + GPU status
                return (byte) (statusRegister & 0xFF);
            case 0x02: // scroll Y
                return (byte) (scy & 0xFF);
            case 0x03: // scroll X
                return (byte) (scx & 0xFF);
            case 0x04: // current scanline
                return (byte) (line & 0xFF);
            case 0x06:
                return (byte) (dmaRegister & 0xFF);
            default:
                return 0;
        }
    }

    public void write(int address, byte b) {

        switch (address) {
            case 0x00: // LCD + GPU control
                controlRegister = b & 0xFF;
                break;
            case 0x01: // LCD + GPU status
                statusRegister = b & 0xFF;
                break;
            case 0x02: // scroll Y
                scy = b & 0xFF;
                break;
            case 0x03: // scroll X
                scx = b & 0xFF;
                break;
            case 0x06: // DMA transfer
                dmaRegister = b & 0xFF;
                break;
            case 0x07: // background palette
                break;
        }
    }

    public void readScanLineToBuffer() {
        int tilemapNum = getBitFromControlRegister(BitValues.TILEMAP);
        int tilesetNum = getBitFromControlRegister(BitValues.TILESET);
        TileMap tileMap = tilemapNum == 1 ? tilemap1 : tilemap0;
        Tileset tileset = tilesetNum == 1 ? tileset1 : tileset0;

        int tileLine = ((((line + scy) & 0xFF) >>> 3) * 32);
        int tileRow = (scx % 256) / 8;
        int x = (scx % 256) % 8;
        int y = ((line + scy) % 256) % 8;
        int tileN = 0;
        try {
            tileN = tileMap.getTileNumber(tileLine + tileRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tile tile = tileset.getTile(tileN);
        Arrays.fill(buffer, (byte) 0);
        for (int pixel = 0; pixel < 160; pixel++) {
            try {
                buffer[pixel] = tile.getPixel(x, y);
            } catch(Exception e) {
                Log.i("Error loading tile", "Tile line: " + tileLine + ", tileRow: " + tileRow + ", tileset: " + tilesetNum + ", tilemap: " + tilemapNum + ", tileXY: " + x + "|" + y);
            }
            x++;
            if(x == 8) {
                tileRow = (tileRow + 1) % 32;
                try {
                    tile = tileset.getTile(tileMap.getTileNumber(tileLine + tileRow));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Line", line + " | " + scy);
                }
                x = 0;
            }
        }
        frame[line] = buffer.clone();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawFrame() {
        screen.setData(frame);
        //Arrays.stream(frame).forEach(row -> Arrays.fill(row, (byte) 0));
    }

    public byte readVram(int address) {
        if(mode == GPUmode.VRAM)
            return (byte) 0xFF;
        else
            return vram[address];
    }

    public void updateTileset(Tileset tileset, boolean isOne, int address) {
        int tileN;
        address = address & 0xFFFF;
        tileN = address >>> 4;
        if(isOne) {
            for (int i = 0; i < 8; i++) {
                tileBuffer[2 * i] = vram[tileN * 0x10 + 2 * i];
                tileBuffer[2 * i + 1] = vram[tileN * 0x10 + 2 * i + 1];
            }
        } else {
            for (int i = 0; i < 8; i++) {
                tileBuffer[2 * i] = vram[0x0800 + tileN * 0x10 + 2 * i];
                tileBuffer[2 * i + 1] = vram[0x0800 + tileN * 0x10 + 2 * i + 1];
            }
        }
        tileset.updateTile(tileN, tileBuffer);
    }

    public void updateTilemap(TileMap tileMap, int address, byte b) {
        tileMap.update(address, b);
    }

    public void writeVram(int address, byte b) {
        if(mode != GPUmode.VRAM) {
            address = address & 0xFFFF;
            vram[address] = b;
            //tileset 1
            if (address < 0x1000) {
                updateTileset(tileset1, true, address);
            }
            //tileset 0
            if (address >= 0x0800 && address < 0x1800) {
                updateTileset(tileset0, false, address - 0x0800);
            }
            //tilemap 0
            if (address >= 0x1800 && address < 0x1C00) {
                updateTilemap(tilemap0, address - 0x1800, b);
            }
            //tilemap 1
            if (address >= 0x1C00) {
                updateTilemap(tilemap1, address - 0x1C00, b);
            }
        }
    }

    public byte readOam(int address) {
        return oam[address];
    }

    public void writeOam(int address, byte b) {
        oam[address] = b;
    }

    public void step(int times) {
        while(times-- > 0){
            clock++;
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

                        readScanLineToBuffer();
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
                            mode = GPUmode.OAM;
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

    public Screen getScreen() {
        return screen;
    }
}
