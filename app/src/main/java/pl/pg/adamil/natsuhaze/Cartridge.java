package pl.pg.adamil.natsuhaze;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Cartridge {

    private boolean isLoaded;
    private byte[] rom;
    private String filePath;

    public Cartridge(String filePath) {
        this.filePath = filePath;
        isLoaded = false;
        rom = new byte[32 * 1024];
    }

    public boolean isCartLoaded() {
        return isLoaded;
    }

    public void loadRom() {
        // Environment
        //        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //        + "/testrom2.gb";
        File file = new File(filePath);
        int size = (int) file.length();
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(rom, 0, rom.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isLoaded = true;
    }

    public byte[] getRomData() {
        return rom.clone();
    }

    public String getTitle() {
        byte[] titleBytes = Arrays.copyOfRange(rom,0x0134,0x0143);
        return new String(titleBytes);
    }
}
