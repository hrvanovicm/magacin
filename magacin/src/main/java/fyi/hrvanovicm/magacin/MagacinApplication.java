package fyi.hrvanovicm.magacin;

import org.hibernate.annotations.processing.SQL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class MagacinApplication {
    public static void main(String[] args) {
        SpringApplication.run(MagacinApplication.class, args);
    }
}
