package com.watchify.app.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudConfig {

    @Bean
    public Cloudinary cloudinary() {

        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", "dxlkhiepo");
        config.put("api_key", "779799824411379");
        config.put("api_secret", "TX3PTSLL4eYbvvXaYPu-rVCa9RA");

        return new Cloudinary(config);
    }
}
