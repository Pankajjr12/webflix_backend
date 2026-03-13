package com.watchify.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.watchify.app.services.FileUploadService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {

        String url = fileUploadService.storeVideoFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("filename", file.getOriginalFilename());
        response.put("size", String.valueOf(file.getSize()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {

        String url = fileUploadService.storeImageFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("filename", file.getOriginalFilename());
        response.put("size", String.valueOf(file.getSize()));

        return ResponseEntity.ok(response);
    }

}
