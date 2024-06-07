package uz.datalab.verifix.hikgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HikgatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(HikgatewayApplication.class, args);
    }
}

// Tomcat application build
//@SpringBootApplication
//public class HikgatewayApplication extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication.run(HikgatewayApplication.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(HikgatewayApplication.class);
//    }
//}