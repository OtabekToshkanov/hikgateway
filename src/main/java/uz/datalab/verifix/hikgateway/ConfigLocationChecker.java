package uz.datalab.verifix.hikgateway;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

//@Component
public class ConfigLocationChecker {
//    @PostConstruct
    public void checkEnv() {
        System.out.println("-".repeat(50));
        String configLocation = System.getenv("SPRING_CONFIG_LOCATION");
        System.out.println("SPRING_CONFIG_LOCATION: " + configLocation);
        System.out.println("-".repeat(50));
    }
}
