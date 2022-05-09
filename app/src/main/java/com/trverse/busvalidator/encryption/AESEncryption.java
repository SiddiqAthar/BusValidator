package com.trverse.busvalidator.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

private static AESEncryption shared;
private String privateKey = "1236547896541236";
private String ivString = "METROISB_0987689";


private AESEncryption(){
}

public static AESEncryption getShared() {
    if (shared != null ){
        return shared;
    }else{
        shared = new AESEncryption();
        return shared;
    }
}

public String encrypt(String value) {
    try {
        IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(value.getBytes());
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return null;
}


public String decrypt(String encrypted) {
    try {
        IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
         cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = new byte[0];
        original = cipher.doFinal(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT));

        return new String(original);
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    return null;
}
}