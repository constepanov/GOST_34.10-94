import java.math.BigInteger;
import java.util.Random;

class GOSTSignature {

    private BigInteger p;
    private BigInteger q;
    private BigInteger a;

    GOSTSignature(BigInteger p, BigInteger q, BigInteger a) {
        this.p = p;
        this.q = q;
        this.a = a;
    }

    BigInteger getPublicKey(BigInteger x) {
        return a.modPow(x, p);
    }

    BigInteger getRandomPrivateKey(int bitLength) {
        if(bitLength < 0 || bitLength < 128 || bitLength > q.bitLength()) {
            throw new IllegalArgumentException("Wrong key length");
        }
        Random r = new Random();
        BigInteger result;
        do {
            result = new BigInteger(bitLength, r);
        } while (result.compareTo(BigInteger.ZERO) < 0 || result.compareTo(q) > 0);
        return result;
    }

    BigInteger getPrivateKeyByPassword(String passwordHash) {
        return new BigInteger(passwordHash, 16);
    }

    String sign(String hash, BigInteger x) {
        BigInteger h = new BigInteger(hash, 16);
        if(h.mod(q).equals(BigInteger.ZERO)) {
            h = BigInteger.ONE;
        }
        BigInteger r;
        BigInteger s;
        BigInteger k;
        do {
            do {
                k = new BigInteger(q.bitLength(), new Random());
            } while (k.compareTo(BigInteger.ZERO) < 0 || k.compareTo(q) > 0);
            r = a.modPow(k, p).mod(q);
            s = x.multiply(r).add(k.multiply(h)).mod(q);
        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));
        String rString = addPadding(r.toString(16));
        String sString = addPadding(s.toString(16));
        return rString + sString;
    }

    boolean verify(String hash, String signature, BigInteger y) {
        String rString = signature.substring(0, signature.length() / 2);
        String sString = signature.substring(signature.length() / 2);
        BigInteger r = new BigInteger(rString, 16);
        BigInteger s = new BigInteger(sString, 16);
        if(r.compareTo(BigInteger.ZERO) <= 0) {
            return false;
        }
        BigInteger h = new BigInteger(hash, 16);
        if(h.mod(q).equals(BigInteger.ZERO)) {
            h = BigInteger.ONE;
        }
        BigInteger v = h.modPow(q.subtract(BigInteger.TWO), q);
        BigInteger z1 = s.multiply(v).mod(q);
        BigInteger z2 = q.subtract(r).multiply(v).mod(q);
        BigInteger u = a.modPow(z1, p).multiply(y.modPow(z2, p)).mod(p).mod(q);
        System.out.println("r = " + r);
        System.out.println("u = " + u);
        return r.equals(u);
    }

    private String addPadding(String input) {
        StringBuilder inputBuilder = new StringBuilder(input);
        for(int i = input.length(); i < 64; i++) {
            inputBuilder.insert(0, "0");
        }
        return inputBuilder.toString();
    }
}
