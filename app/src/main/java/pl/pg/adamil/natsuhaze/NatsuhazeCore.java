package pl.pg.adamil.natsuhaze;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.net.URISyntaxException;

public class NatsuhazeCore {

    private final Context main;
    private Uri fileUri = null;
    private String filePath;

    private CPU cpu;
    private GPU gpu;
    private Input input;
    private Screen screen;
    private Cartridge cart;

    private boolean isPaused;
    private boolean isRunning;
    private boolean changedRom;

    public NatsuhazeCore(Context main) {
        this.main = main;
        isPaused = false;
        changedRom = false;
        isRunning = false;
    }

    public void init() {
        gpu = new GPU(screen);
        cpu = new CPU(this, gpu);
        reset();
    }

    public void reset() {
        cart = new Cartridge(filePath);
        gpu.init();
        cpu.init();

        cart.loadRom();
        cpu.loadCart(cart);
        screen.setText(cpu.getGameTitle());
    }

    public void startScreen() {
        screen = new Screen(main);
    }

    public Screen getScreen() {
        return screen;
    }

    public CPU getCPU() {
        return cpu;
    }

    public GPU getGPU() {
        return gpu;
    }

    public void mainLoop() {
        while(!isRunning) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(fileUri != null) {
                try {
                    filePath = FileUtils.getFilePath(main, fileUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                init();
                isRunning = true;
                changedRom = false;
            }
        }
        while(isRunning) {
            if(changedRom) {
                reset();
                changedRom = false;
            }
            if(!isPaused && cart.isCartLoaded()) {
                cpu.step();
                gpu.step(4);
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadCart(Uri fileUri) {
        try {
            this.fileUri = fileUri;
            filePath = FileUtils.getFilePath(main, fileUri);
            changedRom = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void destroy() {

    }
}
