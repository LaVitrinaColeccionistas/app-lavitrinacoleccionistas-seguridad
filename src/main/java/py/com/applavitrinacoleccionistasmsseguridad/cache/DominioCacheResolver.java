package py.com.applavitrinacoleccionistasmsseguridad.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import java.util.Collection;
import java.util.List;

public class DominioCacheResolver implements CacheResolver {

    public static final String NOMBRE = "dominioCacheResolver";

    private final CacheManager cacheManager;

    public DominioCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> contexto) {
        if (!(contexto.getTarget() instanceof CacheDominio servicio)) {
            throw new IllegalStateException("El servicio " + contexto.getTarget().getClass().getSimpleName()
                    + " debe implementar CacheDominio para usar cache");
        }
        return List.of(cacheManager.getCache(servicio.dominioCache()));
    }
}
