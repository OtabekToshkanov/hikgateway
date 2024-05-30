package uz.datalab.verifix.hikgateway.core;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.hik.HikClient;
import uz.datalab.verifix.hikgateway.client.vhr.VHRClient;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Command;
import uz.datalab.verifix.hikgateway.entity.Middleware;
import uz.datalab.verifix.hikgateway.service.AppService;

@RequiredArgsConstructor
@Setter
@Component
@Scope("prototype")
public class GatewayWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GatewayWorker.class);
    private final VHRClient vhrClient;
    private final HikClient hikClient;
    private final AppService appService;
    private Command command;
    private Middleware middleware;

    @Override
    public void run() {
        try {
//            switch (command.getCommandKind()) {
//                case DEVICE_LIST:
//                    System.out.println("DEVICE_LIST");
//                    break;
//                case DEVICE_ADD:
//                    runAddDevice();
//                    break;
//                case SET_FACE_RECOGNITION_MODE:
//                    System.out.println("SET_FACE_RECOGNITION_MODE");
//                    break;
//                case GET_FACE_RECOGNITION_MODE:
//                    System.out.println("GET_FACE_RECOGNITION_MODE");
//                    break;
//                case ACS_EVENT:
//                    System.out.println("ACS_EVENT");
//                    break;
//                case DEVICE_CAPABILITIES:
//                    System.out.println("DEVICE_CAPABILITIES");
//                    break;
//                case ADD_USER:
//                    System.out.println("ADD_USER");
//                    break;
//                case UPDATE_USER:
//                    System.out.println("UPDATE_USER");
//                    break;
//                case USER_COUNT:
//                    System.out.println("USER_COUNT");
//                    break;
//                case SEARCH_USER:
//                    System.out.println("SEARCH_USER");
//                    break;
//                case DELETE_USER:
//                    System.out.println("DELETE_USER");
//                    break;
//                case DELETE_PROCESS:
//                    System.out.println("DELETE_PROCESS");
//                    break;
//                case FACE_DATA_RECORD:
//                    System.out.println("FACE_DATA_RECORD");
//                    break;
//                case SUBSCRIBE_DEVICE_MGMT:
//                    System.out.println("SUBSCRIBE_DEVICE_MGMT");
//                    break;
//            }
        } catch (Exception e) {
            log.error("Unexpected error occurred while executing command, host: {}, command: {}", middleware.getHost(), command, e);
        }
    }

    public void runAddDevice() {
//        VhrDeviceAddResponse vhrResponse = vhrClient.loadDevice(middleware, command.getCommandId());
//        HikRequest request = HikUtil.createHikRequest(vhrResponse);
//        HikResult result = hikClient.makeDeviceAddRequest(request);
//
//        if (result.isSuccess()) {
//            ((DeviceAddResponse) result.getOutput()).getDeviceOutList()
//                    .stream().map(DeviceOutListItem::getHikDevice)
//                    .forEach(device -> {
//                        try {
//                            if (device.isSuccessful())
//                                middlewareService.createDevice(device.getDeviceIndex(), device.getEhomeParams().getEhomeID(), middleware);
//                        } catch (Exception e) {
//                            log.error("Error occurred while saving device to database, host: {}, deviceIndex: {}, ehomeID: {}",
//                                    middleware.getHost(), device.getDeviceIndex(), device.getEhomeParams().getEhomeID(), e);
//                        }
//                    });
//        }
//
//        vhrClient.notifyCommandExecutionResult(middleware, command, result);
    }
}