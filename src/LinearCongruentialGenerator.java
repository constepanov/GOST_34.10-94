import java.util.stream.LongStream;
import static java.util.stream.LongStream.iterate;

class LinearCongruentialGenerator {

    private long seed;
    private long a;
    private long c;
    private long m;

    LinearCongruentialGenerator(long seed, long a, long c, long m) {
        this.seed = seed;
        this.a = a;
        this.c = c;
        this.m = m;
    }

    LongStream rand() {
        return iterate(seed, x -> (a * x + c) % m).skip(1);
    }

    void setSeed(long seed) {
        this.seed = seed;
    }

    long getSeed() {
        return seed;
    }
}
