package pl.pg.adamil.natsuhaze;

import android.util.Log;

import java.util.Arrays;

public class Memory {
    private CPU cpu;
    private GPU gpu;

    private byte[] data;
    private byte[] wram;
    private byte serialByte;

    public Memory(CPU cpu, GPU gpu) {
        data = new byte[64 * 1024];
        wram = new byte[8 * 1024];
        this.cpu = cpu;
        this.gpu = gpu;
    }

    public void loadFromCart(Cartridge cart) {
        data = Arrays.copyOf(cart.getRomData(), 64 * 1024);
    }

    public byte readByte(int address) {
        address = address & 0xFFFF;
        switch(address & 0xF000) {

            // BIOS (256b)/ROM0
            case 0x0000:
                return data[address & 0xFFFF];

            // ROM0 & ROM1
            case 0x1000:
            case 0x2000:
            case 0x3000:
            case 0x4000:
            case 0x5000:
            case 0x6000:
            case 0x7000:
                return data[address & 0xFFFF];

            // Graphics: VRAM (8k)
            case 0x8000:
            case 0x9000:
                return gpu.readVram(address - 0x8000);

            // External RAM (8k)
            case 0xA000:
            case 0xB000:
                break; // address & 0x1FFF;

            // Working RAM (8k)
            case 0xC000:
            case 0xD000:
                return wram[address - 0xC000];

            // Working RAM echo
            case 0xE000:
                return wram[address - 0xE000];

            // Working RAM echo, I/O, Zero-page RAM
            case 0xF000:
                switch(address & 0x0F00)
                {
                    // Working RAM echo
                    case 0x000: case 0x100: case 0x200: case 0x300:
                    case 0x400: case 0x500: case 0x600: case 0x700:
                    case 0x800: case 0x900: case 0xA00: case 0xB00:
                    case 0xC00: case 0xD00:
                        return wram[address - 0xE000];

                    // Graphics: object attribute memory
                    // OAM is 160 bytes, remaining bytes read as 0
                    case 0xE00:
                        if((address & 0x00FF) < 0x009F)
                            return gpu.readOam(address & 0x00FF);
                        else
                            return 0;

                        // Zero-page
                    case 0xF00:
                        if((address & 0xFF) >= 0x80)
                        {
                            return data[address & 0xFFFF];// zram[addr & 0x7F];
                        }
                        else if((address & 0xFF) >= 0x40 && (address & 0xFF) <= 0x47)
                        {
                            // I/O ports for gpu
                            return gpu.read(address & 0x0F);
                        } else {
                            switch(address & 0xFF) {


                                default:
                                    break;
                            }
                        }
                }
        }
        return 0;
    }

    public short readWord(int address) {
        short high = (short) (readByte(address + 1) << 8);
        return (short) (high + readByte(address));
    }

    public void writeByte(int address, byte b) {
        address = address & 0xFFFF;
        switch(address & 0xF000) {
            // BIOS (256b)/ROM0
            case 0x0000:
                data[address & 0xFFFF] = b;
                break;

            // ROM0 & ROM1
            case 0x1000:
            case 0x2000:
            case 0x3000:
            case 0x4000:
            case 0x5000:
            case 0x6000:
            case 0x7000:
                data[address & 0xFFFF] = b;
                break;

            // Graphics: VRAM (8k)
            case 0x8000:
            case 0x9000:
                gpu.writeVram(address - 0x8000, b);
                break;

            // External RAM (8k)
            case 0xA000:
            case 0xB000:
                data[address & 0xFFFF] = b;
                break; // address & 0x1FFF;

            // Working RAM (8k)
            case 0xC000:
            case 0xD000:
                wram[address - 0xC000] = b;
                break; // address & 0x1FFF;

            // Working RAM echo
            case 0xE000:
                wram[address - 0xE000] = b;
                break; // address & 0x1FFF;

            case 0xF000:
                switch(address & 0x0F00) {
                    // Working RAM echo
                    case 0x000: case 0x100: case 0x200: case 0x300:
                    case 0x400: case 0x500: case 0x600: case 0x700:
                    case 0x800: case 0x900: case 0xA00: case 0xB00:
                    case 0xC00: case 0xD00:
                        wram[address - 0xE000] = b;
                        break;

                    // Graphics: object attribute memory
                    // OAM is 160 bytes, remaining bytes read as 0
                    case 0xE00:
                        if((address & 0xFF) < 0xA0) {
                            gpu.writeOam(address & 0xFF, b);
                            break;
                        } else
                            break;

                        // Zero-page

                    case 0xF00:

                        if((address & 0xFF) >= 0x80)
                        {
                            data[address & 0xFFFF] = b;
                            break;// zram[addr & 0x7F];
                        }
                        else if((address & 0xFF) >= 0x40 && (address & 0xFF) <= 0x47)
                        {
                            if ((address & 0xFF) == 0x46)
                                transferToOam(b);
                            // I/O ports for gpu
                            gpu.write(address & 0x0F, b);
                            break;
                        } else {
                            // I/O ports
                            switch(address & 0xFF) {
                                case 0x04:
                                    data[address & 0xFFFF] = 0;
                                    break;

                                default:
                                    break;
                            }
                        }
                        break;
                }
                break;
        }
    }

    public void transferToOam(byte offset) {
        int source = ((offset & 0xFF) << 8) & 0xFFFF;
        for (int destination = 0; destination < 160; destination++, source++) {
            gpu.writeOam(destination, data[source]);
        }
    }

    public void writeWord(int address, Short high, Short low) {
        writeByte(address, low.byteValue());
        writeByte(address + 1, high.byteValue());
    }
}
