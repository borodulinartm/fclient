package ru.iu3.backend.tools;

import org.springframework.security.crypto.codec.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс - полезные дополнительные утилиты
 * @author artem
 */
public class Utils {
    public static String ComputeHash(String pwd, String salt) {
        MessageDigest digest;

        //System.out.println(pwd.getBytes());

        // IntelliSense требует обёртки в виде String.wrap
        byte[] w = Hex.decode(new String(Hex.encode(pwd.getBytes())) + salt);
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            return new String();
        }

        return new String(Hex.encode(digest.digest(w)));
    }
}
