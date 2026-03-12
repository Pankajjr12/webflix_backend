package com.watchify.app.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatResponse {

    private long totalVideos;
    private long publshedVideos;
    private long totalDuration;
}
