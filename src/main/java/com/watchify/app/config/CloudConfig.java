package com.watchify.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudConfig {

    @Bean
    public Cloudinary cloudinary() {

        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", "dxlkhiepo",
                        "api_key", "779799824411379",
                        "api_secret", "TX3PTSLL4eYbvvXaYPu-rVCa9RA",
                        "secure", true
                )
        );
    }
}
