package pl.pg.adamil.natsuhaze;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class NatsuhazeCore {

    private final Context main;

    private CPU cpu;
    private GPU gpu;
    private Input input;
    private Screen screen;
    private Cartridge cart;

    private boolean isPaused;
    private boolean isRunning;

    public NatsuhazeCore(Context main) {
        this.main = main;
        isPaused = false;
    }

    public void init() {
        screen = new Screen(main);
        gpu = new GPU(screen);
        cpu = new CPU(this, gpu);
        cart = new Cartridge(cpu);
        cart.loadRom();
        screen.init();
        gpu.init();
        cpu.init();
        cpu.loadCart(cart);
        screen.setText(cpu.getGameTitle());
        isRunning = true;
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
        while(isRunning) {
            if(!isPaused && cart.isCartLoaded()) {
                cpu.step();
            }
        }
    }

    public void destroy() {

    }
}
