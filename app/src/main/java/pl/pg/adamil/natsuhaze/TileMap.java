package pl.pg.adamil.natsuhaze;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

    public int getTileNumber(int num) throws Exception {
        return tiles.get(num);
    }

    public void update(int tileN, byte b) {
        tiles.put(tileN, b & 0xFF);
    }
}
