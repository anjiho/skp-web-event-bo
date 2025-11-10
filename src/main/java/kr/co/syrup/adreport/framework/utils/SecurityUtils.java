package kr.co.syrup.adreport.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static String encryptSHA256(String str) {
        String sha = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());

            return bytesToHex(md.digest());

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Encrypt Error - NoSuchAlgorithmException");
        }
        return sha;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(">>" + encryptSHA256("1234"));
    }
}
