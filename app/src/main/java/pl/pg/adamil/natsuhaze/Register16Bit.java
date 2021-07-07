package pl.pg.adamil.natsuhaze;

public class Register16Bit extends Number implements Comparable<Short> {
    private short value;

    public Register16Bit(short s) {
        value = s;
    }

    @Override
    public int compareTo(Short o) {
        return Short.compare(value, o);
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public void add(Short s) {
        value += s;
    }

    public void addHigh(Byte b) {
        byte high = (byte) ((byte) (value >>> 8) + b);
        value = (short) ((short) (high << 8) + (value & 0x00FF));
    }

    public void addLow(Byte b) {
        byte low = (byte) ((byte) (value & 0x00FF) + b);
        value = (short) ((short) (value & 0xFF00) + (short) low);
    }

    public void sub(Short s) {
        value -= s;
    }

    public void subHigh(Byte b) {
        byte high = (byte) ((byte) (value >>> 8) - b);
        value = (short) ((short) (high << 8) + (value & 0x00FF));
    }

    public void subLow(Byte b) {
        byte low = (byte) ((byte) (value & 0x00FF) - b);
        value = (short) ((short) (value & 0xFF00) + (short) low);
    }

    public void set(Short s) {
        value = s;
    }

    public void setHigh(Byte high) {
        value = (short) ((short) (high << 8) + (value & 0x00FF));
    }

    public void setLow(Byte low) {
        value = (short) ((short) (value & 0xFF00) + (short) low);
    }

    public void inc() {
        value += 1;
    }

    public void dec() {
        value -= 1;
    }

    public byte getHigh() {
        int i = value >>> 8; // unsigned shift
        return (byte) i;
    }

    public byte getLow() {
        int i =  value & 0x00FF;
        return (byte) i;
    }
}
