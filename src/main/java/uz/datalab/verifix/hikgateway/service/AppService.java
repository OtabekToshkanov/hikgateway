package uz.datalab.verifix.hikgateway.service;

import uz.datalab.verifix.hikgateway.dto.MiddlewareDTO;
import uz.datalab.verifix.hikgateway.entity.Device;

import java.util.List;

public interface AppService {
    void createMiddleware(MiddlewareDTO middlewareDTO);

    List<MiddlewareDTO> getMiddlewares();

    MiddlewareDTO getMiddleware(long id);

    void updateMiddleware(MiddlewareDTO middlewareDTO);

    void deleteMiddleware(long id);

    void createDevice(String deviceIndex, long vhrId, long middlewareId);

    List<Device> getDevices(long clientId);

    void updateDevice(long id, String deviceIndex, long vhrId);

    void deleteDevice(long id);

    void deleteDeviceByMiddlewareIdAndVhrId(long middlewareId, long vhrId);
}
