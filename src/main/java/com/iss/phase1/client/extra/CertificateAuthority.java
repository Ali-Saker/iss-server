package com.iss.phase1.client.extra;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateAuthority {

    static {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File publicKeyFile = Paths.get(System.getProperty("java.io.tmpdir"),
                    "ISSPublicKey").toFile();

            File privateKeyFile = Paths.get(System.getProperty("java.io.tmpdir"),
                    "ISSPrivateKey").toFile();

            new ObjectOutputStream(new FileOutputStream(publicKeyFile)).writeObject(key.getPublic());

            new ObjectOutputStream(new FileOutputStream(privateKeyFile)).writeObject(key.getPrivate());

        } catch (Exception ex) {
            throw new RuntimeException("Unable to initialize the certificate authority!", ex);
        }

    }

    private static PublicKey getPublicKey() {
        PublicKey publicKey = null;
        try {
            File publicKeyFile = Paths.get(System.getProperty("java.io.tmpdir"),
                    "ISSPublicKey").toFile();
            publicKey = (PublicKey) new ObjectInputStream(new FileInputStream(publicKeyFile)).readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load the certificate authority's public key!", ex);
        }
        return publicKey;
    }

    private static PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        try {
            File privateKeyFile = Paths.get(System.getProperty("java.io.tmpdir"),
                    "ISSPrivateKey").toFile();
            privateKey = (PrivateKey) new ObjectInputStream(new FileInputStream(privateKeyFile)).readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load the certificate authority's private key!", ex);
        }
        return privateKey;
    }


    public static X509Certificate generateV1Certificate(PublicKey PK, String additionalData)
            throws InvalidKeyException, SignatureException {

        if(!additionalData.equals("ISSSERVER")) {
            throw new RuntimeException("Unable to verify the requester identity!");
        }
        Security.addProvider(new BouncyCastleProvider());
        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X509Principal("CN=SERVER"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 500000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 500000));
        certGen.setSubjectDN(new X509Name("CN=" + additionalData));
        certGen.setPublicKey(PK);

        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        return certGen.generateX509Certificate(getPrivateKey());
    }

    public static boolean verifyCertificate(X509Certificate certificate) {
        try {
            certificate.verify(getPublicKey());
            certificate.checkValidity();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

/*
    public static void main(String[] args) throws Exception {
        // create the keys
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        KeyPair pair2 = keyGen.generateKeyPair();

        // generate the certificate
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate cert = generateV1Certificate(pair2.getPublic(), pair.getPrivate(), "eid");


        cert.verify(pair.getPublic());
        // show some basic validation
        cert.checkValidity();

//        cert.verify(cert.getPublicKey());

         System.out.println(new String(cert.getEncoded()));
    }
    */
}
