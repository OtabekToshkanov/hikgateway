package uz.datalab.verifix.hikgateway.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.hik.HikClient;
import uz.datalab.verifix.hikgateway.client.hik.entity.HikResult;
import uz.datalab.verifix.hikgateway.client.hik.entity.HikUtil;
import uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.response.DeviceAddResponse;
import uz.datalab.verifix.hikgateway.client.hik.entity.devicedel.response.DeviceDelResponse;
import uz.datalab.verifix.hikgateway.client.vhr.VHRClient;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Command;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Commands;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Photo;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.SetPhotoCommandBody;
import uz.datalab.verifix.hikgateway.client.vhr.entity.save.CommandResult;
import uz.datalab.verifix.hikgateway.client.vhr.entity.save.CommandsResult;
import uz.datalab.verifix.hikgateway.entity.Middleware;
import uz.datalab.verifix.hikgateway.service.AppService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);
    private final AppService appService;
    private final VHRClient vhrClient;
    private final HikClient hikClient;
    private final ObjectMapper objectMapper;

    public void executeCommands(Middleware middleware, long deviceId, Commands commands) {
        commands.getCommands().forEach(command -> {
            CommandResult commandResult = executeCommand(middleware, deviceId, command);
            CommandsResult result = new CommandsResult(commandResult);
            vhrClient.saveCommands(middleware, result);
        });
    }

    public CommandsResult executeCommandsConcurrently(Middleware middleware, long deviceId, Commands commands) {
        CommandsResult result = new CommandsResult();

        // Create a virtual thread executor
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Create a list of CompletableFutures for executing each command asynchronously
            List<CompletableFuture<CommandResult>> futures = commands.getCommands().stream()
                    .map(command -> CompletableFuture.supplyAsync(() -> executeCommand(middleware, deviceId, command), executor)
                            .exceptionally(e -> {
                                log.error("Error occurred while executing command, commandId: {}", command.getCommandId(), e);
                                return new CommandResult(command.getCommandId(), makeErrorResponse("Unexpected executor error", "executorFailed"), 500);
                            }))
                    .toList();

            // Wait for all futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Collect results
            futures.forEach(future -> {
                try {
                    result.addCommand(future.join());
                } catch (Exception e) {
                    log.error("Error occurred while collecting command result by joining future", e);
                }
            });

            executor.shutdown();
        } catch (Exception e) {
            log.error("Error occurred while executing commands, middlewareId: {}, deviceId: {}", middleware.getId(), deviceId, e);
        }

        return result;
    }

    private CommandResult executeCommand(Middleware middleware, long deviceId, Command command) {
        CommandResult commandResult = new CommandResult(command.getCommandId());

        try {
            switch (command.getExecutionMode()) {
                case "transmission" -> executeTransmissionMode(command, commandResult, middleware, deviceId);
                case "operation" -> executeOperationMode(command, commandResult, middleware);
                default -> {
                    log.warn("Command execution mode is not supported, commandId: {}, mode: {}", command.getCommandId(), command.getExecutionMode());
                    prepareErrorCommandResult(commandResult, 400, "Unsupported execution mode", "executionModeFailed");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while executing command, commandId: {}", command.getCommandId(), e);
            prepareErrorCommandResult(commandResult, 500, "Unexpected executor error", "executorFailed");
        }

        return commandResult;
    }

    private void executeTransmissionMode(Command command, CommandResult commandResult, Middleware middleware, long deviceId) {
        HikResult hikResult = hikClient.makeSimpleRequest(command.getCommandRoute(), command.getHttpMethod(), command.getCommandBody());
        prepareSuccessCommandResult(commandResult, hikResult.getCode(), hikResult.getOutput());

        if (!hikResult.isSuccess()) return;

        switch (command.getCommandCode()) {
            case "hikvision:device:add":
                addDevice(middleware, deviceId, hikResult.getOutput());
                break;
            case "hikvision:device:remove":
                removeDevice(middleware, deviceId, hikResult.getOutput());
                break;
        }
    }

    private void executeOperationMode(Command command, CommandResult commandResult, Middleware middleware) {
        if (command.getCommandCode().equals("hikvision:person:set_photo")) {
            SetPhotoCommandBody commandBody = objectMapper.convertValue(command.getCommandBody(), SetPhotoCommandBody.class);
            Photo photo = vhrClient.loadPhoto(middleware, commandBody.getFaceImage());

            if (photo == null) {
                prepareErrorCommandResult(commandResult, 400, "Load photo from vhr failed", "loadPhotoFailed");
            } else {
                HikResult hikResult = hikClient.makeSetPhotoRequest(command.getCommandRoute(), commandBody.getFaceDataRecord(), photo);
                prepareSuccessCommandResult(commandResult, hikResult.getCode(), hikResult.getOutput());
            }
        } else {
            log.warn("Command code is not supported. commandId: {}, code: {}", command.getCommandId(), command.getExecutionMode());
            prepareErrorCommandResult(commandResult, 400, "Unsupported command code", "commandCodeFailed");
        }
    }

    private void addDevice(Middleware middleware, long deviceId, String hikResultOutput) {
        try {
            DeviceAddResponse response = objectMapper.readValue(hikResultOutput, DeviceAddResponse.class);

            response.getDeviceOutList().forEach(deviceOutListItem -> {
                if (deviceOutListItem.getDevice().isSuccessful())
                    appService.createDevice(deviceOutListItem.getDevice().getDeviceIndex(), deviceId, middleware.getId());
            });
        } catch (Exception e) {
            log.error("Error occurred while adding device to middleware, middlewareId: {}, deviceId: {}", middleware.getId(), deviceId, e);
        }
    }

    private void removeDevice(Middleware middleware, long deviceId, String hikResultOutput) {
        try {
            DeviceDelResponse response = objectMapper.readValue(hikResultOutput, DeviceDelResponse.class);

            response.getDelListItems().forEach(delListItem -> {
                if (delListItem.getDevice().isSuccessful())
                    appService.deleteDeviceByMiddlewareIdAndVhrId(middleware.getId(), deviceId);
            });
        } catch (Exception e) {
            log.error("Error occurred while removing device from middleware, middlewareId: {}, deviceId: {}", middleware.getId(), deviceId, e);
        }
    }

    private void prepareSuccessCommandResult(CommandResult result, int responseCode, String commandResult) {
        result.setResponseCode(responseCode);
        result.setCommandResult(commandResult);
    }

    private void prepareErrorCommandResult(CommandResult result, int errorCode, String errorMsg, String subStatusCode) {
        result.setResponseCode(errorCode);
        result.setCommandResult(makeErrorResponse(errorMsg, subStatusCode));
    }

    private String makeErrorResponse(String errorMsg, String subStatusCode) {
        try {
            return objectMapper.writeValueAsString(HikUtil.makeErrorResponse(errorMsg, subStatusCode));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}