package com.watchify.app.servicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.watchify.app.dao.UserRepository;
import com.watchify.app.dao.VideoRepository;
import com.watchify.app.models.User;
import com.watchify.app.models.Video;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.VideoResponse;
import com.watchify.app.services.WatchlistService;
import com.watchify.app.utils.PaginationUtils;
import com.watchify.app.utils.ServiceUtils;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceUtils serviceUtils;

    @Override
    public MessageResponse addToWatchlist(String email, Long videoId) {

        User user = serviceUtils.getUserByEmailOrThrow(email);
        Video video = serviceUtils.getVideoByIdOrThrow(videoId);

        user.addToWatchList(video);

        userRepository.save(user);

        return new MessageResponse("Video added to watchlist successfully");
    }

    @Override
    public MessageResponse removeFromWatchlist(String email, Long videoId) {

        User user = serviceUtils.getUserByEmailOrThrow(email);
        Video video = serviceUtils.getVideoByIdOrThrow(videoId);

        user.removeFromWatchList(video);

        userRepository.save(user);

        return new MessageResponse("Video removed from watchlist successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VideoResponse> getWatchlistPaginated(int page, int size, String search, String email) {

        User user = serviceUtils.getUserByEmailOrThrow(email);

        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Video> videoPage;

        if (search != null && !search.trim().isEmpty()) {
            videoPage = userRepository.searchWatchlistByUserId(user.getId(), search.trim(), pageable);
        } else {
            videoPage = userRepository.findWatchlistByUserId(user.getId(), pageable);
        }

        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
    }
}
