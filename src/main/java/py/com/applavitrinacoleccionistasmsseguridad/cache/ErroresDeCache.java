package py.com.applavitrinacoleccionistasmsseguridad.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class ErroresDeCache implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.warn("[cache:{}] error en GET {}: {}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object valor) {
        log.warn("[cache:{}] error en PUT {}: {}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
        log.warn("[cache:{}] error en EVICT {}: {}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.warn("[cache:{}] error en CLEAR: {}", cache.getName(), e.getMessage());
    }
}
