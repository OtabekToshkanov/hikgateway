package uz.datalab.verifix.hikgateway.dto;

import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.entity.Middleware;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class DTOUtil {
    public Middleware toMiddlewareEntity(MiddlewareDTO middlewareDTO) {
        Middleware middleware = new Middleware();
        middleware.setId(middlewareDTO.getId());
        middleware.setHost(middlewareDTO.getHost());
        middleware.setToken(middlewareDTO.getToken());

        String credentials = Base64.getEncoder()
                .encodeToString((middlewareDTO.getUsername() + ":" + middlewareDTO.getPassword()).getBytes(StandardCharsets.UTF_8));

        middleware.setCredentials(credentials);

        return middleware;
    }

    public MiddlewareDTO toMiddlewareDTO(Middleware middleware) {
        String[] credentials = new String(Base64.getDecoder().decode(middleware.getCredentials())).split(":");

        return new MiddlewareDTO.MiddlewareDTOBuilder()
                .id(middleware.getId())
                .host(middleware.getHost())
                .token(middleware.getToken())
                .username(credentials[0])
                .password(credentials[1])
                .build();
    }
}