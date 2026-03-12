package com.watchify.app.responses;

import java.time.Instant;
import java.util.List;

import com.watchify.app.models.Video;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoResponse {

    private Long id;
    private String title;
    private String description;
    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;

    private boolean published;

    private List<String> categories;

    private Instant createdAt;
    private Instant updatedAt;
    private boolean isInWatchlist;

    public VideoResponse(Long id, String title, String description, Integer year, String rating, Integer duration,
            String src, String poster, boolean published, List<String> categories, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.duration = duration;
        this.src = src;
        this.poster = poster;
        this.published = published;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public static VideoResponse fromEntity(Video video) {
        VideoResponse videoResponse = new VideoResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getYear(),
                video.getRating(),
                video.getDuration(),
                video.getSrc(),
                video.getPoster(),
                video.isPublished(),
                video.getCategories(),
                video.getCreatedAt(),
                video.getUpdatdAt()
        );
        if (video.getIsInWatchlist() != null) {
            videoResponse.setInWatchlist(video.getIsInWatchlist());
        }

        return videoResponse;
    }

}
