package py.com.applavitrinacoleccionistasmsseguridad;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheConfig;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheProperties;
import py.com.applavitrinacoleccionistasmsseguridad.cache.LoggingCacheManager;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IRolRepository;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IUsuarioRepository;
import py.com.lavitrinacoleccionistas.dto.UsuarioDTO;
import py.com.lavitrinacoleccionistas.entity.Rol;
import py.com.lavitrinacoleccionistas.enums.EstadoUsuario;
import py.com.lavitrinacoleccionistas.enums.TipoRol;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(roles = "ADMIN")
class CacheSeguridadIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired CacheManager cacheManager;
    @Autowired CacheProperties propiedades;
    @Autowired IRolRepository rolRepository;
    @Autowired IUsuarioRepository usuarioRepository;
    @Autowired TransactionTemplate tx;

    private Long idRol;

    @BeforeAll
    void rolesFijos() {
        if (rolRepository.count() == 0) {
            for (TipoRol tipo : TipoRol.values()) {
                Rol rol = new Rol();
                rol.setTipoRol(tipo);
                rolRepository.save(rol);
            }
        }
        idRol = rolRepository.findAll().get(0).getId();
    }

    private Cache usuarios() {
        return cacheManager.getCache(propiedades.dominios().usuario());
    }

    private Cache roles() {
        return cacheManager.getCache(propiedades.dominios().rol());
    }

    private long crearUsuario(String email) throws Exception {
        MvcResult r = mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"clave12345\",\"idRoles\":[" + idRol + "]}"))
                .andExpect(status().isCreated()).andReturn();
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(r.getResponse().getContentAsString());
        m.find();
        return Long.parseLong(m.group(1));
    }

    @Test
    void usuarioSeGuardaAlCrearSeLeeDelCacheSeActualizaYSeInvalida() throws Exception {
        long id = crearUsuario("cache-uno@mail.com");
        assertThat(usuarios().get("id:" + id)).as("se guarda al crear").isNotNull();

        tx.executeWithoutResult(s -> usuarioRepository.findById(id).orElseThrow().setEstado(EstadoUsuario.RECHAZADO));
        mvc.perform(get("/usuarios/" + id)).andExpect(jsonPath("$.estado", is("ACTIVO")));

        mvc.perform(put("/usuarios/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"cache-dos@mail.com\",\"idRoles\":[" + idRol + "]}"))
                .andExpect(status().isOk());
        assertThat(((UsuarioDTO) usuarios().get("id:" + id).get()).getEmail()).isEqualTo("cache-dos@mail.com");

        mvc.perform(delete("/usuarios/" + id)).andExpect(status().isNoContent());
        assertThat(usuarios().get("id:" + id)).as("el borrado logico invalida la entrada").isNull();
    }

    @Test
    void losListadosNoSeCacheanPeroSusElementosSi() throws Exception {
        long id = crearUsuario("cache-lista@mail.com");
        usuarios().clear();
        roles().clear();

        mvc.perform(get("/usuarios").param("email", "cache-lista")).andExpect(status().isOk());
        mvc.perform(get("/roles")).andExpect(status().isOk());

        assertThat(usuarios().get("id:" + id)).as("cada usuario del listado se guarda por separado").isNotNull();
        assertThat(roles().get("id:" + idRol)).as("cada rol del listado se guarda por separado").isNotNull();
        Map<?, ?> contenido = (Map<?, ?>) usuarios().getNativeCache();
        assertThat(contenido.keySet()).allMatch(k -> k.toString().startsWith("id:"));
    }

    @Test
    void cadaDominioEsIndependiente() throws Exception {
        long id = crearUsuario("cache-dominios@mail.com");
        mvc.perform(get("/roles/" + idRol)).andExpect(status().isOk());
        assertThat(roles().get("id:" + idRol)).isNotNull();

        usuarios().clear();
        assertThat(usuarios().get("id:" + id)).isNull();
        assertThat(roles().get("id:" + idRol)).as("invalidar usuarios no toca roles").isNotNull();
    }

    @Test
    void politicaDeRetencionYPrefijoPorDominio() {
        LettuceConnectionFactory conexion = new LettuceConnectionFactory("localhost", 6379);
        RedisCacheManager redis = (RedisCacheManager) ((LoggingCacheManager)
                new CacheConfig().cacheManager(conexion, propiedades)).getDelegado();

        RedisCache usuariosRedis = (RedisCache) redis.getCache(propiedades.dominios().usuario());
        assertThat(usuariosRedis.getCacheConfiguration().getTtlFunction().getTimeToLive("k", "v"))
                .isEqualTo(Duration.ofMinutes(15));
        assertThat(usuariosRedis.getCacheConfiguration().getKeyPrefixFor("usuario")).isEqualTo("lvc:seguridad:usuario::");

        RedisCache rolesRedis = (RedisCache) redis.getCache(propiedades.dominios().rol());
        assertThat(rolesRedis.getCacheConfiguration().getTtlFunction().getTimeToLive("k", "v")).isEqualTo(Duration.ZERO);
    }
}
