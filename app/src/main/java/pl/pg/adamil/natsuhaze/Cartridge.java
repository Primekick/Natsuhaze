package pl.pg.adamil.natsuhaze;

public class Cartridge {

    private CPU cpu;

    private boolean isLoaded;

    public Cartridge(CPU cpu) {
        this.cpu = cpu;
    }

    public boolean isCartLoaded() {
        return isLoaded;
    }
}
