package uz.datalab.verifix.hikgateway.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.hik.HikClient;
import uz.datalab.verifix.hikgateway.client.hik.entity.HikErrorResponse;
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

import java.util.*;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Setter
@Component
@Scope("prototype")
public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);
    private final AppService appService;
    private final VHRClient vhrClient;
    private final HikClient hikClient;
    private final ObjectMapper objectMapper;
    private ExecutorService executor;
    private Middleware middleware;
    private long deviceId;

    @PostConstruct
    public void init() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }

    public boolean executeCommands(Commands commands) {
        if (commands.getCommands() == null || commands.getCommands().isEmpty()) return false;

        switch (commands.getOperationMode()) {
            case "parallel" -> executeCommandsConcurrently(commands.getCommands(), commands.getDelayAttemptTimes());
            case "sequential", "sequencial" -> executeCommandsSequentially(commands.getCommands());
            default -> {
                log.error("Invalid operation mode: {}", commands.getOperationMode());
                return false;
            }
        }

        return true;
    }

    private void executeCommandsConcurrently(List<Command> commands, int[] delayAttemptTimes) {
        CompletionService<CommandResult> completionService = new ExecutorCompletionService<>(executor);
        CountDownLatch latch = new CountDownLatch(commands.size());

        commands.forEach(command -> completionService.submit(() -> {
            try {
                CommandResult commandResult = executeCommand(command);

                // if command execution failed due to internal error or device busy, return commandId for retry
                // otherwise save command result to VHR
                try {
                    if (commandResult.isPossibleInternalOrDeviceBusyError()) {
                        HikErrorResponse errorResponse = objectMapper.readValue(commandResult.getCommandResult(), HikErrorResponse.class);

                        if ("internalError".equals(errorResponse.getSubStatusCode()) ||
                                "deviceBusy".equals(errorResponse.getSubStatusCode())) {
                            return commandResult;
                        }
                    }
                } catch (Exception ignored) {
                }

                vhrClient.saveCommands(middleware, new CommandsResult(commandResult));
                return null;
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Error occurred while waiting for command completion", e);
            Thread.currentThread().interrupt();
        }

        List<CommandResult> failedCommandResults = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            try {
                Future<CommandResult> future = completionService.poll(1, TimeUnit.SECONDS);
                if (future != null && future.get() != null)
                    failedCommandResults.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred while getting command result", e);
            }
        }

        if (!failedCommandResults.isEmpty()) {
            Map<String, Command> commandMap = new HashMap<>();
            commands.forEach(command -> commandMap.put(command.getCommandId(), command));

            List<Command> failedCommands = failedCommandResults.stream()
                    .map(commandResult -> commandMap.get(commandResult.getCommandId()))
                    .filter(Objects::nonNull)
                    .toList();

            executeCommandsSequentiallyWithDelayAfterFail(failedCommands, failedCommandResults, delayAttemptTimes);
        }
    }

    private void executeCommandsSequentiallyWithDelayAfterFail(List<Command> commands, List<CommandResult> failedCommandResults, int[] delayAttemptTimes) {
        int delayIndex = 0;

        for (int i = 0; i < commands.size(); i++) {
            // if delay attempt times are exhausted, return error for remaining commands
            if (delayIndex >= delayAttemptTimes.length || delayAttemptTimes[delayIndex] >= 15 * 60) {
                CommandsResult result = new CommandsResult();

                for (int j = i; j < commands.size(); j++) result.addCommand(failedCommandResults.get(j));

                vhrClient.saveCommands(middleware, result);
                break;
            }

            CommandResult commandResult = executeCommand(commands.get(i));

            try {
                if (commandResult.isPossibleInternalOrDeviceBusyError()) {
                    HikErrorResponse errorResponse = objectMapper.readValue(commandResult.getCommandResult(), HikErrorResponse.class);

                    if ("internalError".equals(errorResponse.getSubStatusCode()) ||
                            "deviceBusy".equals(errorResponse.getSubStatusCode())) {
                        Thread.sleep(delayAttemptTimes[delayIndex++] * 1000L);
                        i--;
                        continue;
                    }
                }
            } catch (Exception ignored) {
            }

            CommandsResult result = new CommandsResult(commandResult);
            vhrClient.saveCommands(middleware, result);
        }
    }

    private void executeCommandsSequentially(List<Command> commands) {
        commands.forEach(command -> {
            CommandResult commandResult = executeCommand(command);
            CommandsResult result = new CommandsResult(commandResult);
            vhrClient.saveCommands(middleware, result);
        });
    }

    private CommandResult executeCommand(Command command) {
        CommandResult commandResult = new CommandResult(command.getCommandId());

        try {
            switch (command.getExecutionMode()) {
                case "transmission" -> executeTransmissionMode(command, commandResult);
                case "operation" -> executeOperationMode(command, commandResult);
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

    private void executeTransmissionMode(Command command, CommandResult commandResult) {
        HikResult hikResult = hikClient.makeSimpleRequest(command.getCommandRoute(), command.getHttpMethod(), command.getCommandBody());
        prepareSuccessCommandResult(commandResult, hikResult.getCode(), hikResult.getOutput());

        if (!hikResult.isSuccess()) return;

        switch (command.getCommandCode()) {
            case "hikvision:device:add":
                addDevice(hikResult.getOutput());
                break;
            case "hikvision:device:remove":
                removeDevice(hikResult.getOutput());
                break;
        }
    }

    private void executeOperationMode(Command command, CommandResult commandResult) {
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

    private void addDevice(String hikResultOutput) {
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

    private void removeDevice(String hikResultOutput) {
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