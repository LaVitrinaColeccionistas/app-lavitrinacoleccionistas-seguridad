package py.com.applavitrinacoleccionistasmsseguridad.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Bean
    @ConditionalOnProperty(prefix = "app.cache", name = "tipo", havingValue = "redis", matchIfMissing = true)
    public CacheManager cacheManager(RedisConnectionFactory conexion, CacheProperties propiedades) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith(propiedades.prefijo())
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(serializador(propiedades)))
                .entryTtl(propiedades.ttlDefecto());
        Map<String, RedisCacheConfiguration> porDominio = new HashMap<>();
        propiedades.ttl().forEach((dominio, ttl) -> porDominio.put(dominio, base.entryTtl(ttl)));
        RedisCacheManager redis = RedisCacheManager.builder(conexion)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(porDominio)
                .build();
        redis.afterPropertiesSet();
        return new LoggingCacheManager(redis);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.cache", name = "tipo", havingValue = "memoria")
    public CacheManager cacheManagerMemoria() {
        return new LoggingCacheManager(new ConcurrentMapCacheManager());
    }

    @Bean(DominioCacheResolver.NOMBRE)
    public CacheResolver dominioCacheResolver(CacheManager cacheManager) {
        return new DominioCacheResolver(cacheManager);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new ErroresDeCache();
    }

    private GenericJacksonJsonRedisSerializer serializador(CacheProperties propiedades) {
        BasicPolymorphicTypeValidator.Builder validador = BasicPolymorphicTypeValidator.builder();
        propiedades.paquetesPermitidos().forEach(validador::allowIfSubType);
        return GenericJacksonJsonRedisSerializer.builder().enableDefaultTyping(validador.build()).build();
    }
}
