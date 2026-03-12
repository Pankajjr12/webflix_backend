package com.watchify.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.VideoResponse;
import com.watchify.app.services.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/{videoId}")
    public ResponseEntity<MessageResponse> addToWatchlist(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.addToWatchlist(email, videoId));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<MessageResponse> removeFromWatchlist(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.removeFromWatchlist(email, videoId));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<VideoResponse>> getWatchlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        String email = authentication.getName();
        PageResponse<VideoResponse> response = watchlistService.getWatchlistPaginated(page, size, search, email);
        return ResponseEntity.ok(response);
    }

}
