package br.com.coffeeandit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder(App.class)
        .profiles("default")
        .properties("management.info.git.mode:full")
        .properties("server.port:8080");
    applicationBuilder.run(args);
  }

}
