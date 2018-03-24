import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import static java.lang.Math.pow;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MD4 md4 = new MD4();
        System.out.println("Input file:");
        File inputFile = new File(scanner.nextLine());
        System.out.println("Action:");
        System.out.println("1. Generate signature");
        System.out.println("2. Verify signature");
        int action = Integer.parseInt(scanner.nextLine());
        if(action == 1) {
            LinearCongruentialGenerator g = new LinearCongruentialGenerator(
                    0x3DFC46F1,
                    97781173,
                    0xD,
                    (long) pow(2, 32)
            );
            Generator generator = new Generator(g);
            BigInteger[] primes = generator.generatePrimes1024();
            BigInteger p = primes[0];
            BigInteger q = primes[1];
            BigInteger a = generator.generateA(p, q);
            System.out.println("File for p, q, a:");
            String fileName = scanner.nextLine();
            File file = new File(fileName);
            String params = p.toString(16) + "\n" + q.toString(16) + "\n" + a.toString(16);
            writeToFile(file, params);
            GOSTSignature ds = new GOSTSignature(p, q, a);
            System.out.println("Enter the length of the key:");
            int length = Integer.parseInt(scanner.nextLine());
            BigInteger x = ds.generatePrivateKey(length);
            BigInteger y = ds.generatePublicKey(x);
            System.out.println("File for public key:");
            fileName = scanner.nextLine();
            File publicKey = new File(fileName);
            writeToFile(publicKey, y.toString(16));
            byte[] message = readFromFile(inputFile);
            md4.engineUpdate(message, 0, message.length);
            String hash = md4.toHexString(md4.engineDigest());
            String signature = ds.sign(hash, x);
            System.out.println("File for signature:");
            fileName = scanner.nextLine();
            file = new File(fileName);
            writeToFile(file, signature);
            System.out.println("Hash:" + hash);
            System.out.println("Signature:" + signature);
        } else if(action == 2) {
            System.out.println("File with p, q, a:");
            File file = new File(scanner.nextLine());
            byte[] data = readFromFile(file);
            String[] params = new String(data).split("\n");
            BigInteger p = new BigInteger(params[0], 16);
            BigInteger q = new BigInteger(params[1], 16);
            BigInteger a = new BigInteger(params[2], 16);
            System.out.println("File with public key:");
            file = new File(scanner.nextLine());
            data = readFromFile(file);
            BigInteger y = new BigInteger(new String(data), 16);
            GOSTSignature ds = new GOSTSignature(p, q, a);
            data = readFromFile(inputFile);
            md4.engineUpdate(data, 0, data.length);
            String hash = md4.toHexString(md4.engineDigest());
            System.out.println("File with signature:");
            file = new File(scanner.nextLine());
            data = readFromFile(file);
            String signature = new String(data);
            System.out.println("Hash:" + hash);
            System.out.println("Verification of a signature: ");
            boolean result = ds.verify(hash, signature, y);
            if(result) {
                System.out.print("OK");
            } else {
                System.out.print("FAIL");
            }
        } else {
            System.out.println("Unknown action");
        }
    }

    private static void writeToFile(File file, String data) {
        try {
            Files.write(file.toPath(), data.getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFromFile(File file) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}