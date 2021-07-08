package pl.pg.adamil.natsuhaze;

public class Tile {

    private byte[][] tileData;

    public Tile() {
        tileData = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tileData[i][j] = 0;
            }
        }
    }

    public Tile(byte[] tileBytes) {
        tileData = new byte[8][8];
        updateTile(tileBytes);
    }

    public void updateTile(byte[] tileBytes) {
        for (int i = 0; i < 8; i++) {
            byte byteLow = tileBytes[2 * i];
            byte byteHigh = tileBytes[2 * i + 1];
            for (int j = 0; j < 8; j++) {
                int x = (byteHigh >>> (7 - j)) & 1;
                x = x << 1;
                int y = (byteLow >>> (7 - j)) & 1;
                tileData[i][j] = (byte) ((x + y) & 0xFF);
            }
        }
    }

    public byte getPixel(int x, int y) {
        return tileData[y][x];
    }
}
