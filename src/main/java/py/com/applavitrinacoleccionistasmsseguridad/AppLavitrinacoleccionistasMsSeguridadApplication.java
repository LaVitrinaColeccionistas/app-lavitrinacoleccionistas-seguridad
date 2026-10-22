package py.com.applavitrinacoleccionistasmsseguridad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("py.com.lavitrinacoleccionistas.entity")
@EnableJpaRepositories("py.com.applavitrinacoleccionistasmsseguridad.repository")
public class AppLavitrinacoleccionistasMsSeguridadApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppLavitrinacoleccionistasMsSeguridadApplication.class, args);
    }

}
