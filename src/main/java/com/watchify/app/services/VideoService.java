package com.watchify.app.services;

import java.util.List;

import com.watchify.app.requests.VideoRequest;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.VideoResponse;
import com.watchify.app.responses.VideoStatResponse;

import jakarta.validation.Valid;

public interface VideoService {

    MessageResponse createVideoByAdmin(VideoRequest videoRequest);

    PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);

    MessageResponse updateVideoByAdmin(Long id, @Valid VideoRequest videoRequest);

    MessageResponse deleteVideoByAdmin(Long id);

    MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean value);

    VideoStatResponse getAdminStats();

    PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email);

    List<VideoResponse> getFeaturedVideos();
}
