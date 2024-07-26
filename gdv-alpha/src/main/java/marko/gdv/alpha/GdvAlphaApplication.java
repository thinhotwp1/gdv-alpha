package marko.gdv.alpha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GdvAlphaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GdvAlphaApplication.class, args);
    }

}
