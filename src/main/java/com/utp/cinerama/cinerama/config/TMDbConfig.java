package com.utp.cinerama.cinerama.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de TMDb API
 * Asegúrate de agregar la API KEY en application.properties
 */
@Configuration
@Getter
public class TMDbConfig {

    @Value("${tmdb.api.key:YOUR_API_KEY_HERE}")
    private String apiKey;

    @Value("${tmdb.api.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;

    @Value("${tmdb.api.language:es-MX}")
    private String language;

    @Value("${tmdb.api.region:PE}")
    private String region;

    // Endpoints específicos
    public String getNowPlayingUrl() {
        return baseUrl + "/movie/now_playing";
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
