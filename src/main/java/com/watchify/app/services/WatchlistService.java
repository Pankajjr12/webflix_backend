package com.watchify.app.services;

import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.VideoResponse;

public interface WatchlistService {

    MessageResponse addToWatchlist(String email, Long videoId);

    MessageResponse removeFromWatchlist(String email, Long videoId);

    PageResponse<VideoResponse> getWatchlistPaginated(int page, int size, String search, String email);

}
