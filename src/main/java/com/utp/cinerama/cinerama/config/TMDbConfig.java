package com.utp.cinerama.cinerama.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TMDbConfig {

    @Value("${tmdb.api.key:}")
    private String apiKey;

    @Value("${tmdb.api.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;

    @Value("${tmdb.api.language:es-MX}")
    private String language;

    @Value("${tmdb.api.region:PE}")
    private String region;

    
    public String getNowPlayingUrl() {
        return baseUrl + "/movie/now_playing";
    }

    public String getUpcomingUrl() {
        return baseUrl + "/movie/upcoming";
    }

    public String getPopularUrl() {
        return baseUrl + "/movie/popular";
    }

    public String getMovieDetailsUrl(Long movieId) {
        return baseUrl + "/movie/" + movieId;
    }

    public String getGenresUrl() {
        return baseUrl + "/genre/movie/list";
    }
}
