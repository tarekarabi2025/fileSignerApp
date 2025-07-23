package com.tarek.filesignerapp.model;

import java.security.cert.X509Certificate;

public class KeyFilesResponse {
    private final byte[] publicKey;
    private final byte[] privateKey;
    private final byte[] keystore;
    private final X509Certificate certificate;

    public KeyFilesResponse(byte[] publicKey, byte[] privateKey, byte[] keystore, X509Certificate certificate) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.keystore = keystore;
        this.certificate = certificate;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public byte[] getKeystore() {
        return keystore;
    }
    public X509Certificate getCertificate() { return certificate; }
}
