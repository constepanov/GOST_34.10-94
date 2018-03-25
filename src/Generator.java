import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

class Generator {

    private LinearCongruentialGenerator generator;

    Generator(LinearCongruentialGenerator generator) {
        this.generator = generator;
    }

    private BigInteger[] generatePrimes512() {
        int bitLength = 512;
        List<Integer> t = new ArrayList<>();
        t.add(bitLength);
        int index = 0;
        while(t.get(index) >= 33) {
            int value = (int) floor(t.get(index) / 2);
            t.add(value);
            index++;
        }
        BigInteger[] primes = new BigInteger[t.size()];
        primes[index] = BigInteger.probablePrime(t.get(index), new Random());
        int m = index - 1;
        boolean flag = true;
        do {
            int r = (int) Math.ceil(t.get(m) / 32);
            BigInteger n = BigInteger.ZERO;
            BigInteger k = BigInteger.ZERO;
            do {
                if(flag) {
                    long[] y = generator.rand().limit(r).toArray();
                    BigInteger sum = BigInteger.ZERO;
                    for (int i = 0; i < r - 1; i++) {
                        BigInteger tmp = BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(32));
                        sum = sum.add(tmp);
                    }
                    sum = sum.add(BigInteger.valueOf(generator.getSeed()));
                    generator.setSeed(y[r - 1]);
                    BigInteger tmp1 = new BigDecimal(BigInteger.TWO.pow(t.get(m) - 1))
                            .divide(new BigDecimal(primes[m + 1]), 0, RoundingMode.CEILING)
                            .toBigInteger();
                    BigInteger tmp2 = new BigDecimal(BigInteger.TWO.pow(t.get(m) - 1).multiply(sum))
                            .divide(new BigDecimal(primes[m + 1].multiply(BigInteger.TWO.pow(32 * r))), 0, RoundingMode.FLOOR)
                            .toBigInteger();
                    n = tmp1.add(tmp2);
                    if (!(n.mod(BigInteger.TWO).equals(BigInteger.ZERO))) {
                        n = n.add(BigInteger.ONE);
                    }
                    k = BigInteger.ZERO;
                }
                primes[m] = primes[m + 1].multiply(n.add(k)).add(BigInteger.ONE);
                if(primes[m].compareTo(BigInteger.TWO.pow(t.get(m))) > 0) {
                    flag = true;
                    continue;
                }
                if(!(BigInteger.TWO.modPow(primes[m + 1].multiply(n.add(k)), primes[m]).equals(BigInteger.ONE))
                        || BigInteger.TWO.modPow(n.add(k), primes[m]).equals(BigInteger.ONE)) {
                    flag = false;
                    k = k.add(BigInteger.TWO);
                } else {
                    flag = true;
                    break;
                }
            } while (true);
            m--;
        } while (m >= 0);
        return primes;
    }

    BigInteger[] generatePrimes1024() {
        int bitLength = 1024;
        BigInteger q = generatePrimes512()[1];
        BigInteger Q = generatePrimes512()[0];
        BigInteger p;
        BigInteger n = BigInteger.ZERO;
        BigInteger k = BigInteger.ZERO;
        int yLength = 32;
        boolean flag = true;
        do {
            if(flag) {
                long[] y = generator.rand().limit(yLength).toArray();
                BigInteger sum = BigInteger.ZERO;
                for (int i = 0; i < y.length - 1; i++) {
                    BigInteger tmp = BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(32));
                    sum = sum.add(tmp);
                }
                sum = sum.add(BigInteger.valueOf(generator.getSeed()));
                generator.setSeed(y[y.length - 1]);
                BigInteger tmp1 = new BigDecimal(BigInteger.TWO.pow(bitLength - 1))
                        .divide(new BigDecimal(q.multiply(Q)), 0, RoundingMode.CEILING)
                        .toBigInteger();
                BigInteger tmp2 = new BigDecimal(BigInteger.TWO.pow(bitLength - 1).multiply(sum))
                        .divide(new BigDecimal(q.multiply(Q).multiply(BigInteger.TWO.pow(bitLength))), 0, RoundingMode.FLOOR)
                        .toBigInteger();
                n = tmp1.add(tmp2);
                if (!(n.mod(BigInteger.TWO).equals(BigInteger.ZERO))) {
                    n = n.add(BigInteger.ONE);
                }
                k = BigInteger.ZERO;
            }
            p = q.multiply(Q).multiply(n.add(k)).add(BigInteger.ONE);
            if(p.compareTo(BigInteger.TWO.pow(bitLength)) > 0) {
                flag = true;
                continue;
            }
            if(!(BigInteger.TWO.modPow(q.multiply(Q).multiply(n.add(k)), p).equals(BigInteger.ONE))
                    || BigInteger.TWO.modPow(q.multiply(n.add(k)), p).equals(BigInteger.ONE)) {
                flag = false;
                k = k.add(BigInteger.TWO);
            } else {
                break;
            }
        } while (true);
        return new BigInteger[] {p, q};
    }

    BigInteger generateA(BigInteger p, BigInteger q) {
        BigInteger d;
        BigInteger f;
        do {
            do {
                d = new BigInteger(p.bitLength(), new Random());
            } while(d.compareTo(BigInteger.ONE) <= 0 || d.compareTo(p.subtract(BigInteger.ONE)) >= 0);
            f = d.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        } while (f.equals(BigInteger.ONE));
        return f;
    }
}
