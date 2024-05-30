package uz.datalab.verifix.hikgateway.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.datalab.verifix.hikgateway.dao.AppDAO;
import uz.datalab.verifix.hikgateway.dto.MiddlewareDTO;
import uz.datalab.verifix.hikgateway.dto.DTOUtil;
import uz.datalab.verifix.hikgateway.entity.Middleware;
import uz.datalab.verifix.hikgateway.entity.Device;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AppServiceImpl implements AppService {
    private final AppDAO dao;
    private final DTOUtil util;

    @Override
    @Transactional
    public void createMiddleware(MiddlewareDTO middlewareDTO) {
        dao.createMiddleware(util.toMiddlewareEntity(middlewareDTO));
    }

    @Override
    public List<MiddlewareDTO> getMiddlewares() {
        return dao.findAllMiddleware().stream()
                .map(util::toMiddlewareDTO)
                .toList();
    }

    @Override
    public MiddlewareDTO getMiddleware(long id) {
        return util.toMiddlewareDTO(dao.findMiddlewareById(id));
    }

    @Override
    @Transactional
    public void updateMiddleware(MiddlewareDTO middlewareDTO) {
        dao.updateMiddleware(util.toMiddlewareEntity(middlewareDTO));
    }

    @Override
    @Transactional
    public void deleteMiddleware(long id) {
        dao.deleteMiddlewareById(id);
    }

    @Override
    @Transactional
    public void createDevice(String deviceIndex, long vhrId, long middlewareId) {
        Middleware middlewareRecord = dao.findMiddlewareById(middlewareId);
        Device device = new Device();
        device.setDeviceIndex(deviceIndex);
        device.setVhrId(vhrId);
        device.setMiddleware(middlewareRecord);
        dao.createDevice(device);
    }

    @Override
    public List<Device> getDevices(long clientId) {
        return dao.findAllDevicesByMiddlewareId(clientId);
    }

    @Override
    @Transactional
    public void updateDevice(long id, String deviceIndex, long vhrId) {
        Device device = dao.findDeviceById(id);
        device.setDeviceIndex(deviceIndex);
        device.setVhrId(vhrId);
        dao.updateDevice(device);
    }

    @Override
    @Transactional
    public void deleteDevice(long id) {
        dao.deleteDeviceById(id);
    }

    @Override
    @Transactional
    public void deleteDeviceByMiddlewareIdAndVhrId(long middlewareId, long vhrId) {
        dao.deleteDeviceByMiddlewareIdAndVhrId(middlewareId, vhrId);
    }
}