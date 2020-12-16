package com.iss.phase1.client.extra;

import java.security.PublicKey;
import java.security.Signature;

public class DigitalSignature {


    public static byte [] sign(String text) {
        byte [] signedText = null;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(RSA.getPrivateKey());
            signature.update(text.getBytes());
            signedText = signature.sign();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signedText;
    }


    public static boolean verify(String originalText, byte [] signedText, PublicKey publicKey) {
        boolean isCorrect = false;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(originalText.getBytes());
            isCorrect = signature.verify(signedText);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }

        return isCorrect;
    }

}