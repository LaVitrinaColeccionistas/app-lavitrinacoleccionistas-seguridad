package py.com.applavitrinacoleccionistasmsseguridad.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

@Slf4j
class LoggingCache implements Cache {

    private final Cache delegado;

    LoggingCache(Cache delegado) {
        this.delegado = delegado;
    }

    @Override
    public String getName() {
        return delegado.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegado.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valor = delegado.get(key);
        log.debug("[cache:{}] GET {} -> {}", getName(), key, valor == null ? "MISS" : "HIT");
        return valor;
    }

    @Override
    public <T> T get(Object key, Class<T> tipo) {
        T valor = delegado.get(key, tipo);
        log.debug("[cache:{}] GET {} -> {}", getName(), key, valor == null ? "MISS" : "HIT");
        return valor;
    }

    @Override
    public <T> T get(Object key, Callable<T> cargador) {
        log.debug("[cache:{}] GET {} (con carga si falta)", getName(), key);
        return delegado.get(key, cargador);
    }

    @Override
    public void put(Object key, Object valor) {
        delegado.put(key, valor);
        log.debug("[cache:{}] PUT {}", getName(), key);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object valor) {
        ValueWrapper previo = delegado.putIfAbsent(key, valor);
        log.debug("[cache:{}] PUT-IF-ABSENT {} -> {}", getName(), key, previo == null ? "guardado" : "ya existia");
        return previo;
    }

    @Override
    public void evict(Object key) {
        delegado.evict(key);
        log.debug("[cache:{}] EVICT {}", getName(), key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        boolean existia = delegado.evictIfPresent(key);
        log.debug("[cache:{}] EVICT {} -> {}", getName(), key, existia ? "eliminado" : "no estaba");
        return existia;
    }

    @Override
    public void clear() {
        delegado.clear();
        log.debug("[cache:{}] CLEAR (dominio completo)", getName());
    }

    @Override
    public boolean invalidate() {
        boolean invalidado = delegado.invalidate();
        log.debug("[cache:{}] INVALIDATE -> {}", getName(), invalidado);
        return invalidado;
    }
}
