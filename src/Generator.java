import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

class Generator {

    private static long x;
    private static long y0;
    private static long module = (long) Math.pow(2, 32);

    private static BigInteger[] generatePrimes512() {
        int bitLength = 512;
        int[] t = new int[5];
        t[0] = bitLength;
        int s = 0;
        for(int i = 0; i < t.length; i++) {
            if(t[i] >= 33) {
                t[i + 1] = (int) Math.floor(t[i] / 2);
            } else {
                s = i;
                break;
            }
        }

        BigInteger[] primes = new BigInteger[5];
        primes[4] = BigInteger.probablePrime(t[4], new Random());
        int m = s - 1;
        long c = 0xD;
        if(x == 0) {
            x = 0x3DFC46F1;
        }
        y0 = x;
        boolean flag = true;
        do {
            int r = (int) Math.ceil(t[m] / 32);
            long[] y = new long[r + 1];
            BigInteger n = BigInteger.ZERO;
            BigInteger k = BigInteger.ZERO;
            do {
                if(flag) {
                    y[0] = y0;
                    for (int i = 0; i < y.length - 1; i++) {
                        y[i + 1] = (97781173 * y[i] + c) % module;
                    }
                    BigInteger sum = BigInteger.ZERO;
                    for (int i = 0; i < r + 1; i++) {
                        BigInteger tmp = BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(32));
                        sum = sum.add(tmp);
                    }
                    y0 = y[r];
                    BigInteger tmp1 = new BigDecimal(BigInteger.TWO.pow(t[m] - 1))
                            .divide(new BigDecimal(primes[m + 1]), 0, RoundingMode.CEILING)
                            .toBigInteger();
                    BigInteger tmp2 = new BigDecimal(BigInteger.TWO.pow(t[m] - 1).multiply(sum))
                            .divide(new BigDecimal(primes[m + 1].multiply(BigInteger.TWO.pow(32 * r))), 0, RoundingMode.FLOOR)
                            .toBigInteger();
                    n = tmp1.add(tmp2);
                    if (!(n.mod(BigInteger.TWO).equals(BigInteger.ZERO))) {
                        n = n.add(BigInteger.ONE);
                    }
                    k = BigInteger.ZERO;
                }
                // Шаг 11
                primes[m] = primes[m + 1].multiply(n.add(k)).add(BigInteger.ONE);
                if(primes[m].compareTo(BigInteger.TWO.pow(t[m])) > 0) {
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

    static BigInteger[] generatePrimes1024() {
        int bitLength = 1024;
        long c = 0xD;
        BigInteger q = generatePrimes512()[1];
        x = y0;
        BigInteger Q = generatePrimes512()[0];
        BigInteger p;
        BigInteger n = BigInteger.ZERO;
        BigInteger k = BigInteger.ZERO;
        long[] y = new long[33];
        boolean flag = true;
        do {
            if(flag) {
                y[0] = y0;
                for (int i = 0; i < y.length - 1; i++) {
                    y[i + 1] = (97781173 * y[i] + c) % module;
                }
                BigInteger sum = BigInteger.ZERO;
                for (int i = 0; i < y.length - 1; i++) {
                    BigInteger tmp = BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(32));
                    sum = sum.add(tmp);
                }
                y0 = y[y.length - 1];
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

    static BigInteger generateA(BigInteger p, BigInteger q) {
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
