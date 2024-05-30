package uz.datalab.verifix.hikgateway.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.datalab.verifix.hikgateway.dto.MiddlewareDTO;
import uz.datalab.verifix.hikgateway.service.AppService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AppController {
    private final AppService appService;

    @PostMapping("/middleware")
    public void createMiddleware(@RequestBody MiddlewareDTO middlewareDTO) {
        appService.createMiddleware(middlewareDTO);
    }

    @GetMapping("/middleware")
    public List<MiddlewareDTO> getMiddleware() {
        return appService.getMiddlewares();
    }

    @GetMapping("/middleware/{id}")
    public MiddlewareDTO getMiddleware(@PathVariable int id) {
        return appService.getMiddleware(id);
    }

    @PutMapping("/middleware")
    public void updateMiddleware(@RequestBody MiddlewareDTO middlewareDTO) {
        appService.updateMiddleware(middlewareDTO);
    }

    @DeleteMapping("/middleware/{id}")
    public void deleteMiddleware(@PathVariable int id) {
        appService.deleteMiddleware(id);
    }
}
