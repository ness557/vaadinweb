package simlpe.microservices.vaadinweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VaadinwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaadinwebApplication.class, args);
    }

    @Bean
    public in.virit.WidgetSet viritinCdnInitializer() {
        return new in.virit.WidgetSet();
    }
}
