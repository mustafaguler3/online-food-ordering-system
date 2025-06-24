package com.example.FoodApp.aws;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface AWSS3Service {
    URL uploadFile(String keyName, MultipartFile file);
    void deleteFile(String keyName);
}
