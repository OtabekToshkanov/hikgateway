package uz.datalab.verifix.hikgateway.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.hik.HikClient;
import uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.response.DeviceListResponse;
import uz.datalab.verifix.hikgateway.client.vhr.VHRClient;
import uz.datalab.verifix.hikgateway.config.VhrProperties;
import uz.datalab.verifix.hikgateway.dao.AppDAO;
import uz.datalab.verifix.hikgateway.entity.Device;
import uz.datalab.verifix.hikgateway.entity.Middleware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class ApplicationScheduler {
    private static final Logger log = LoggerFactory.getLogger(ApplicationScheduler.class);
    private final AppDAO dao;
    private final VhrProperties vhrProperties;
    private final VHRClient vhrClient;
    private final HikClient hikClient;
    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void schedule() {
        try {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(this::run, 0, vhrProperties.getJobInterval(), TimeUnit.SECONDS);
            log.info("Application jobs stated with initial delay of {} seconds and delay of {} seconds", 0, vhrProperties.getJobInterval());
        } catch (Exception e) {
            log.error("Error occurred while scheduling application jobs", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            log.info("Application jobs stopped");
        }
    }

    private void run() {
        try {
            List<Middleware> middlewares = dao.findAllMiddleware();

            List<DeviceInfo> deviceInfos = loadDeviceInfos();

            middlewares.forEach(middleware -> {
                vhrClient.sendHealthCheck(middleware);
                sendDeviceStatuses(deviceInfos, middleware);
            });
        } catch (Exception e) {
            log.error("Health check job failed", e);
        }
    }

    private List<DeviceInfo> loadDeviceInfos() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        int position = 0;
        int totalMatches = 10_000; //

        while (position < totalMatches) {
            DeviceListResponse deviceListResponse = hikClient.makeDeviceListRequest(position);

            deviceListResponse.getSearchResult()
                    .getMatchList()
                    .forEach(deviceWrapper -> deviceInfos.add(new DeviceInfo(deviceWrapper.getDevice().getDevIndex(), deviceWrapper.getDevice().getDevStatus())));

            position += 50;
            totalMatches = deviceListResponse.getSearchResult().getTotalMatches();
        }

        return deviceInfos;
    }

    private void sendDeviceStatuses(List<DeviceInfo> deviceInfos, Middleware middleware) {
        DeviceStatuses deviceStatuses = prepareDeviceStatuses(deviceInfos, middleware);
        vhrClient.sendDeviceStatuses(middleware, deviceStatuses);
    }

    private DeviceStatuses prepareDeviceStatuses(List<DeviceInfo> deviceStatuses, Middleware middleware) {
        List<Device> devices = dao.findAllDevicesByMiddlewareId(middleware.getId());
        DeviceStatuses devicesInfo = new DeviceStatuses();

        devices.forEach(device -> deviceStatuses.stream()
                .filter(deviceStatus -> deviceStatus.devIndex.equals(device.getDeviceIndex()))
                .findFirst()
                .ifPresent(deviceStatus -> devicesInfo.addDeviceInfo(device.getVhrId(), mapStatus(deviceStatus.status))));

        return devicesInfo;
    }

    private String mapStatus(String status) {
        return switch (status) {
            case "online" -> "O";
            case "offline" -> "F";
            case "sleep" -> "S";
            default -> "";
        };
    }

    private record DeviceInfo(String devIndex, String status) {
    }

    private record DeviceStatus(@JsonProperty("device_id") long deviceId, @JsonProperty("status") String status) {
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class DeviceStatuses {
        @JsonProperty("devices")
        private List<DeviceStatus> devicesStatuses;

        public DeviceStatuses() {
            devicesStatuses = new ArrayList<>();
        }

        void addDeviceInfo(long deviceId, String status) {
            if (devicesStatuses == null)
                devicesStatuses = new ArrayList<>();

            devicesStatuses.add(new DeviceStatus(deviceId, status));
        }
    }
}