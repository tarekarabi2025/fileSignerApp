package com.tarek.filesignerapp.model;


import jakarta.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "sign_process")
public class SignProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "private_key_name")
    private String privateKeyName;

    @Column(name = "public_key_name")
    private String publicKeyName;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @Lob
    @Column(name = "public_key_data")
    private byte[] publicKeyData;

    @Lob
    @Column(name = "private_key_data")
    private byte[] privateKeyData;

    @Lob
    @Column(name = "signature")
    private byte[] signature;

    @Column(name = "sign_name")
    private String signName;

    public SignProcess() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrivateKeyName() {
        return privateKeyName;
    }

    public void setPrivateKeyName(String privateKeyName) {
        this.privateKeyName = privateKeyName;
    }

    public String getPublicKeyName() {
        return publicKeyName;
    }

    public void setPublicKeyName(String publicKeyName) {
        this.publicKeyName = publicKeyName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public byte[] getPublicKeyData() {
        return publicKeyData;
    }

    public void setPublicKeyData(byte[] publicKeyData) {
        this.publicKeyData = publicKeyData;
    }

    public byte[] getPrivateKeyData() {
        return privateKeyData;
    }

    public void setPrivateKeyData(byte[] privateKeyData) {
        this.privateKeyData = privateKeyData;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    @Override
    public String toString() {
        return "SignProcess{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", privateKeyName='" + privateKeyName + '\'' +
                ", publicKeyName='" + publicKeyName + '\'' +
                ", fileData=" + Arrays.toString(fileData) +
                ", publicKeyData=" + Arrays.toString(publicKeyData) +
                ", privateKeyData=" + Arrays.toString(privateKeyData) +
                ", signature=" + Arrays.toString(signature) +
                ", signName='" + signName + '\'' +
                '}';
    }
}
