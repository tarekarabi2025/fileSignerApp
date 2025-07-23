package com.tarek.filesignerapp.controller;

import com.tarek.filesignerapp.model.KeyFilesResponse;
import com.tarek.filesignerapp.model.KeyGenerationRequest;
import com.tarek.filesignerapp.service.KeyPairService;
import com.tarek.filesignerapp.service.SignProcessService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class SignProcessController {

    private final SignProcessService signService;
    private final KeyPairService keyPairService;

    public SignProcessController(SignProcessService signService, KeyPairService keyPairService) {
        this.signService = signService;
        this.keyPairService = keyPairService;
    }

    @GetMapping("/")
    public String home(Model model, @ModelAttribute("message") String message) {
        model.addAttribute("signProcesses", signService.allSigns());
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        return "upload";
    }

    @PostMapping("/sign")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               @RequestParam("privateKey") MultipartFile privateKey,
                               @RequestParam("publicKey") MultipartFile publicKey,
                               @RequestParam("signName") String signName,
                               RedirectAttributes redirectAttributes) {
        try {
            signService.uploadAndSign(file, privateKey, publicKey, signName);
            redirectAttributes.addFlashAttribute("message", "✅ File uploaded and signed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "❌ Error: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("file") MultipartFile file,
                         @RequestParam("signatureFile") MultipartFile signatureFile,
                         @RequestParam("publicKeyFile") MultipartFile publicKeyFile,
                         RedirectAttributes redirectAttributes) {
        try {
            boolean verified = signService.verifySignature(file, signatureFile, publicKeyFile);
            if (verified) {
                redirectAttributes.addFlashAttribute("message", "✅ Signature verified successfully!");
            } else {
                redirectAttributes.addFlashAttribute("message", "❌ Signature verification failed!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "❌ Error verifying: " + e.getMessage());
        }
        return "redirect:/";
    }

//    @GetMapping("/generateKeys")
//    public void generateKeys(HttpServletResponse response) throws IOException {
//        response.setContentType("application/zip");
//        response.setHeader("Content-Disposition", "attachment; filename=keyPair.zip");
//
//        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
//            zos.putNextEntry(new ZipEntry("PublicKey.pub"));
//            zos.write(keyPairService.getPublicKeyEncoded());
//            zos.closeEntry();
//
//            zos.putNextEntry(new ZipEntry("PrivateKey.key"));
//            zos.write(keyPairService.getPrivateKeyEncoded());
//            zos.closeEntry();
//        }
//    }

    @PostMapping("/generateCustomKeys")
    public void generateCustomKeys(
            @RequestParam String cn,
            @RequestParam String ou,
            @RequestParam String o,
            @RequestParam String l,
            @RequestParam String s,
            @RequestParam String c,
            @RequestParam String alias,
            @RequestParam String password,
            @RequestParam int keySize,
            @RequestParam int validityDays,
            HttpServletResponse response
    ) throws Exception {
        String dname = String.format("CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s", cn, ou, o, l, s, c);

        KeyGenerationRequest request = new KeyGenerationRequest();
        request.setAlias(alias);
        request.setPassword(password);
        request.setDname(dname);
        request.setKeySize(keySize);
        request.setValidityDays(validityDays);

        KeyFilesResponse result = keyPairService.generateKeyPair(request);

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + alias + "_keys.zip");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            zos.putNextEntry(new ZipEntry("public_key.pub"));
            zos.write(result.getPublicKey());
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("private_key.key"));
            zos.write(result.getPrivateKey());
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("keystore.p12"));
            zos.write(result.getKeystore());
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("certificate.cer"));
            zos.write(result.getCertificate().getEncoded()); // DER format
            zos.closeEntry();
        }
    }



    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        var spOpt = signService.findById(id);
        if (spOpt.isPresent()) {
            var sp = spOpt.get();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + sp.getFileName());
            response.getOutputStream().write(sp.getFileData());
        }
    }

    @GetMapping("/downloadSignature/{id}")
    public void downloadSignature(@PathVariable Long id, HttpServletResponse response) throws IOException {
        var spOpt = signService.findById(id);
        if (spOpt.isPresent()) {
            var sp = spOpt.get();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + sp.getSignName());
            response.getOutputStream().write(sp.getSignature());
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        signService.delete(id);
        redirectAttributes.addFlashAttribute("message", "✅ Deleted successfully");
        return "redirect:/";
    }
}
