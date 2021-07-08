package pl.pg.adamil.natsuhaze;

import java.util.HashMap;

public class Tileset {

    private GPU gpu;
    private byte[] vram;
    private HashMap<Integer, Tile> tiles;
    private boolean isTilesetOne;

    public Tileset(GPU gpu, byte[] vram, boolean isOne) {
        this.gpu = gpu;
        this.vram = vram;
        isTilesetOne = isOne;
        tiles = new HashMap<Integer, Tile>();

        int tileN = isTilesetOne ? 0 : -128;
        int startAddress = isTilesetOne ? 0x0000 : 0x0800;
        for (int i = 0; i < 256; i++) {
            byte[] tileBytes = new byte[16];
            int tileAddress = startAddress + i * 16;
            for (int j = 0; j < 16; j++) {
                tileBytes[j] = vram[tileAddress + j];
            }
            tiles.put(tileN, new Tile(tileBytes));
            tileN++;
        }
    }

    public void updateTile(int tileN, byte[] data) {
        tiles.get(tileN).updateTile(data);
    }

    public Tile getTile(int tileN) {
        return tiles.get(tileN);
    }
}
