package fs16.webide.web_ide_for;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = "fs16.webide.web_ide_for")
public class WebIdeForApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebIdeForApplication.class, args);
	}

}
