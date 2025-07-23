package com.tarek.filesignerapp.service;

import com.tarek.filesignerapp.model.SignProcess;
import com.tarek.filesignerapp.repository.SignProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SignProcessService {

    private final SignProcessRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(SignProcessService.class);

    public SignProcessService(SignProcessRepository repository) {
        this.repository = repository;
    }

    public void uploadAndSign(MultipartFile file,
                              MultipartFile privateKey,
                              MultipartFile publicKey,
                              String signName) throws Exception {

        logger.info("begin create sign");

        SignProcess sign = new SignProcess();
        sign.setFileName(file.getOriginalFilename());
        sign.setPrivateKeyName(privateKey.getOriginalFilename());
        sign.setPublicKeyName(publicKey.getOriginalFilename());
        sign.setSignName(signName);

        sign.setFileData(file.getBytes());
        sign.setPrivateKeyData(privateKey.getBytes());
        sign.setPublicKeyData(publicKey.getBytes());

        // generate digital signature
        Signature signer = Signature.getInstance("SHA256withRSA");

        // load private key from uploaded privateKey
        var kf = KeyFactory.getInstance("RSA");
        var pkcs8Spec = new java.security.spec.PKCS8EncodedKeySpec(privateKey.getBytes());
        PrivateKey privKey = kf.generatePrivate(pkcs8Spec);

        signer.initSign(privKey);
        signer.update(file.getBytes());

        byte[] signatureBytes = signer.sign();
        sign.setSignature(signatureBytes);

        repository.save(sign);

        logger.info("sign saved successfully");
    }





    public List<SignProcess> allSigns() {
        return repository.findAll();
    }

    public Optional<SignProcess> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean verifySignature(MultipartFile file, MultipartFile signatureFile, MultipartFile publicKeyFile) throws Exception {
        // Load the file contents
        byte[] data = file.getBytes();
        byte[] signatureBytes = signatureFile.getBytes();
        byte[] publicKeyBytes = publicKeyFile.getBytes();

        // Validate public key
        var keyFactory = java.security.KeyFactory.getInstance("RSA");
        var pubKeySpec = new java.security.spec.X509EncodedKeySpec(publicKeyBytes);
        var publicKey = keyFactory.generatePublic(pubKeySpec);

        // Verify
        var sig = java.security.Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);

        return sig.verify(signatureBytes);
    }

}
