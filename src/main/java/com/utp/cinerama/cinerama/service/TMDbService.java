package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.config.TMDbConfig;
import com.utp.cinerama.cinerama.dto.TMDbMovieDTO;
import com.utp.cinerama.cinerama.dto.TMDbResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Servicio para consumir la API de TMDb
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TMDbService {

    private final RestTemplate restTemplate;
    private final TMDbConfig tmdbConfig;

    /**
     * Obtiene peliculas en cartelera (now_playing)
     * @param page Numero de pagina (opcional, default: 1)
     * @return Lista de peliculas desde TMDb
     */
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
     * @param page Numero de pagina
     * @return Lista de peliculas populares
     */
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
