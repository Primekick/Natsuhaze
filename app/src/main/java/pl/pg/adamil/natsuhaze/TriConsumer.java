package pl.pg.adamil.natsuhaze;

@FunctionalInterface
public interface TriConsumer<T1, T2, T3> {
    void execute(T1 t1, T2 t2, T3 t3);
}
