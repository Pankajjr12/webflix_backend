package com.watchify.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.watchify.app.dao.UserRepository;
import com.watchify.app.dao.VideoRepository;
import com.watchify.app.exceptions.ResourceNotFoundException;
import com.watchify.app.models.User;
import com.watchify.app.models.Video;

@Component
public class ServiceUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email:" + email));

    }

    public User getUserByIdOrThrow(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));

    }

    public Video getVideoByIdOrThrow(Long id) {
        return videoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

    }

}
