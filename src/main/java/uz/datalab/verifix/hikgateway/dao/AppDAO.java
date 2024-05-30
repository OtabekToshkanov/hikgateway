package uz.datalab.verifix.hikgateway.dao;

import uz.datalab.verifix.hikgateway.entity.Middleware;
import uz.datalab.verifix.hikgateway.entity.Device;

import java.util.List;

public interface AppDAO {
    void createMiddleware(Middleware middleware);
    List<Middleware> findAllMiddleware();
    Middleware findMiddlewareById(long id);
    Middleware findMiddlewareByToken(String token);
    void updateMiddleware(Middleware middleware);
    void deleteMiddlewareById(long id);
    void createDevice(Device device);
    List<Device> findAllDevicesByMiddlewareId(long middlewareId);
    Device findDeviceById(long id);
    void updateDevice(Device device);
    void deleteDeviceById(long id);
    void deleteDeviceByMiddlewareIdAndVhrId(long middlewareId, long vhrId);
}
