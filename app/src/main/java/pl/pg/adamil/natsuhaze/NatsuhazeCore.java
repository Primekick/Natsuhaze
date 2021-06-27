package pl.pg.adamil.natsuhaze;

import android.content.Context;

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
        screen.init();
        gpu.init();
        cpu.init();
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
        if(isRunning) {
            if(!isPaused && cart.isCartLoaded()) {

            }
        }
    }

    public void destroy() {

    }
}
