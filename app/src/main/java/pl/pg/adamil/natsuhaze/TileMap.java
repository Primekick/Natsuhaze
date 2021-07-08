package pl.pg.adamil.natsuhaze;

import android.util.Log;

import java.util.HashMap;

public class TileMap {
    private boolean isMapOne;
    private HashMap<Integer, Integer> tiles;

    public TileMap(boolean isMapOne) {
        this.isMapOne = isMapOne;
        tiles = new HashMap<Integer, Integer>();
        for (int i = 0; i < 32*32; i++) {
            tiles.put(i, 0);
        }
    }

    public int getTileNumber(int num) {
        return tiles.get(num);
    }

    public boolean isMapOne() {
        return isMapOne;
    }

    public void update(int tileN, byte b) {
        if(isMapOne){
            tiles.put(tileN, b & 0xFF);
        } else {
            tiles.put(tileN, Integer.valueOf(b));
        }
    }
}
