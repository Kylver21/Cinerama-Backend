package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.config.TMDbConfig;
import com.utp.cinerama.cinerama.dto.TMDbMovieDTO;
import com.utp.cinerama.cinerama.dto.TMDbResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Servicio para consumir la API de TMDb con cache
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TMDbService {

    private final RestTemplate restTemplate;
    private final TMDbConfig tmdbConfig;

    /**
     * Obtiene peliculas en cartelera (now_playing)
     * Cache de 10 minutos para reducir llamadas a API externa
     * @param page Numero de pagina (opcional, default: 1)
     * @return Lista de peliculas desde TMDb
     */
    @Cacheable(value = "tmdb-now-playing", key = "#page", unless = "#result == null || #result.isEmpty()")
    public List<TMDbMovieDTO> getNowPlayingMovies(Integer page) {
        try {
            String url = buildUrl(tmdbConfig.getNowPlayingUrl(), page);
            log.info("Consultando TMDb API: {}", url);
            
            TMDbResponseDTO response = restTemplate.getForObject(url, TMDbResponseDTO.class);
            
            if (response != null && response.getResults() != null) {
                log.info("Se obtuvieron {} peliculas de TMDb (pagina {})", 
                    response.getResults().size(), page != null ? page : 1);
                return response.getResults();
            }
            
            log.warn("La respuesta de TMDb esta vacia");
            return List.of();
            
        } catch (Exception e) {
            log.error("Error al consultar TMDb API: {}", e.getMessage());
            throw new RuntimeException("Error al obtener películas desde TMDb: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene peliculas populares
     * Cache de 10 minutos
     * @param page Numero de pagina
     * @return Lista de peliculas populares
     */
    @Cacheable(value = "tmdb-popular", key = "#page", unless = "#result == null || #result.isEmpty()")
    public List<TMDbMovieDTO> getPopularMovies(Integer page) {
        try {
            String url = buildUrl(tmdbConfig.getPopularUrl(), page);
            log.info("Consultando peliculas populares en TMDb: {}", url);
            
            TMDbResponseDTO response = restTemplate.getForObject(url, TMDbResponseDTO.class);
            
            if (response != null && response.getResults() != null) {
                log.info("Se obtuvieron {} peliculas populares de TMDb", response.getResults().size());
                return response.getResults();
            }
            
            return List.of();
            
        } catch (Exception e) {
            log.error("Error al consultar peliculas populares: {}", e.getMessage());
            throw new RuntimeException("Error al obtener películas populares: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene peliculas proximamente (upcoming)
     * Cache de 10 minutos
     * @param page Numero de pagina
     * @return Lista de peliculas que se estrenarán pronto
     */
    @Cacheable(value = "tmdb-upcoming", key = "#page", unless = "#result == null || #result.isEmpty()")
    public List<TMDbMovieDTO> getUpcomingMovies(Integer page) {
        try {
            String url = buildUrl(tmdbConfig.getUpcomingUrl(), page);
            log.info("Consultando peliculas proximamente en TMDb: {}", url);
            
            TMDbResponseDTO response = restTemplate.getForObject(url, TMDbResponseDTO.class);
            
            if (response != null && response.getResults() != null) {
                log.info("Se obtuvieron {} peliculas proximamente de TMDb", response.getResults().size());
                return response.getResults();
            }
            
            return List.of();
            
        } catch (Exception e) {
            log.error("Error al consultar peliculas proximamente: {}", e.getMessage());
            throw new RuntimeException("Error al obtener películas próximamente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene detalles completos de una película específica por ID
     * Usado cuando el admin selecciona una película para agregar a cartelera
     * 
     * @param tmdbId ID de la película en TMDb
     * @return Detalles completos de la película (incluye runtime y genres completos)
     */
    public TMDbMovieDTO getMovieDetails(Long tmdbId) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(tmdbConfig.getMovieDetailsUrl(tmdbId))
                    .queryParam("api_key", tmdbConfig.getApiKey())
                    .queryParam("language", tmdbConfig.getLanguage())
                    .toUriString();
            
            log.info("Consultando detalles de película TMDb ID {}: {}", tmdbId, url);
            
            TMDbMovieDTO movie = restTemplate.getForObject(url, TMDbMovieDTO.class);
            
            if (movie != null) {
                log.info("Detalles obtenidos: {} - Duración: {} min", 
                         movie.getTitle(), movie.getRuntime());
                return movie;
            }
            
            throw new RuntimeException("No se encontró la película con ID: " + tmdbId);
            
        } catch (Exception e) {
            log.error("Error al obtener detalles de película {}: {}", tmdbId, e.getMessage());
            throw new RuntimeException("Error al obtener detalles de película desde TMDb: " + e.getMessage(), e);
        }
    }

    /**
     * Construye la URL con parámetros de consulta
     */
    private String buildUrl(String baseUrl, Integer page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("api_key", tmdbConfig.getApiKey())
                .queryParam("language", tmdbConfig.getLanguage())
                .queryParam("region", tmdbConfig.getRegion());
        
        if (page != null && page > 0) {
            builder.queryParam("page", page);
        }
        
        return builder.toUriString();
    }

    /**
     * Mapea géneros de IDs a nombres legibles
     */
    public String mapGenreIdsToNames(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return "Sin clasificar";
        }

        // Mapeo básico de géneros de TMDb
        StringBuilder genres = new StringBuilder();
        for (Integer id : genreIds) {
            String genre = getGenreName(id);
            if (!genre.isEmpty()) {
                if (genres.length() > 0) genres.append(", ");
                genres.append(genre);
            }
        }
        
        return genres.length() > 0 ? genres.toString() : "Sin clasificar";
    }

    /**
     * Mapea lista de géneros completos a String (para detalles de película)
     */
    public String mapGenresToNames(List<TMDbMovieDTO.Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "Sin clasificar";
        }
        
        return genres.stream()
                .map(TMDbMovieDTO.Genre::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin clasificar");
    }

    /**
     * Obtiene el nombre del género por ID
     */
    private String getGenreName(Integer id) {
        return switch (id) {
            case 28 -> "Acción";
            case 12 -> "Aventura";
            case 16 -> "Animación";
            case 35 -> "Comedia";
            case 80 -> "Crimen";
            case 99 -> "Documental";
            case 18 -> "Drama";
            case 10751 -> "Familia";
            case 14 -> "Fantasía";
            case 36 -> "Historia";
            case 27 -> "Terror";
            case 10402 -> "Música";
            case 9648 -> "Misterio";
            case 10749 -> "Romance";
            case 878 -> "Ciencia Ficción";
            case 10770 -> "Película de TV";
            case 53 -> "Suspenso";
            case 10752 -> "Bélica";
            case 37 -> "Western";
            default -> "";
        };
    }
}
