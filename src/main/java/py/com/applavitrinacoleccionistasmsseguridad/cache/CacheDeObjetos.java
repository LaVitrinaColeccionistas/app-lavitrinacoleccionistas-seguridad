package py.com.applavitrinacoleccionistasmsseguridad.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheDeObjetos {

    private final CacheManager cacheManager;

    public CacheDeObjetos(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void guardar(String dominio, Object id, Object valor) {
        try {
            Cache cache = cacheManager.getCache(dominio);
            if (cache != null) {
                cache.put(Claves.objeto(id), valor);
            }
        } catch (RuntimeException e) {
            log.warn("[cache:{}] no se pudo guardar {}: {}", dominio, Claves.objeto(id), e.getMessage());
        }
    }
}
