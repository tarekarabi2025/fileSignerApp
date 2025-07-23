package com.tarek.filesignerapp.service;

import com.tarek.filesignerapp.model.KeyFilesResponse;
import com.tarek.filesignerapp.model.KeyGenerationRequest;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class KeyPairService {

//    private KeyPair keyPair;
//
//    @PostConstruct
//    public void init() throws NoSuchAlgorithmException {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//        this.keyPair = keyPairGenerator.generateKeyPair();
//    }
//
//    public PrivateKey getPrivateKey() {
//        return keyPair.getPrivate();
//    }
//
//    public PublicKey getPublicKey() {
//        return keyPair.getPublic();
//    }
//
//    public byte[] getPrivateKeyEncoded() {
//        return keyPair.getPrivate().getEncoded();
//    }
//
//    public byte[] getPublicKeyEncoded() {
//        return keyPair.getPublic().getEncoded();
//    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyFilesResponse generateKeyPair(KeyGenerationRequest request) throws Exception {
        // 1. Generate key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(request.getKeySize());
        KeyPair keyPair = keyGen.generateKeyPair();

        // 2. Create self-signed certificate
        X500Name dn = new X500Name(request.getDname());
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date start = new Date();
        Date end = new Date(start.getTime() + request.getValidityDays() * 24L * 60 * 60 * 1000);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dn, serial, start, end, dn, keyPair.getPublic());
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));

        // 3. Export keys
        byte[] publicKeyBytes = cert.getPublicKey().getEncoded();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

        // 4. Generate PKCS12 keystore (.p12)
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(
                request.getAlias(),
                keyPair.getPrivate(),
                request.getPassword().toCharArray(),
                new X509Certificate[]{cert}
        );

        ByteArrayOutputStream keystoreOut = new ByteArrayOutputStream();
        keyStore.store(keystoreOut, request.getPassword().toCharArray());
        byte[] keystoreBytes = keystoreOut.toByteArray();

        return new KeyFilesResponse(publicKeyBytes, privateKeyBytes, keystoreBytes,cert);
    }
}
