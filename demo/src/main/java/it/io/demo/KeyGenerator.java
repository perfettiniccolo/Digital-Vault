package it.io.demo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class KeyGenerator {
    public static void main(String[] args) throws IOException {
        byte[] key = new byte[96];
        new SecureRandom().nextBytes(key);

        try (FileOutputStream stream = new FileOutputStream("master-key.txt")){
            stream.write(key);
        }
    }
}
