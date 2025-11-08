package com.utp.cinerama.cinerama.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché en memoria para endpoints de TMDb
 * Reduce llamadas repetitivas a la API externa
 * 
 * Nota: Para TTL automático, agregar dependencia:
 * - spring-boot-starter-cache con Caffeine
 * - O Redis para cache distribuido
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                "tmdb-now-playing", 
                "tmdb-popular", 
                "tmdb-upcoming"
        );
        
        // Cache almacenado en memoria durante toda la ejecución
        // Se puede mejorar con @CacheEvict programado o con Caffeine/Redis
        return cacheManager;
    }
}
