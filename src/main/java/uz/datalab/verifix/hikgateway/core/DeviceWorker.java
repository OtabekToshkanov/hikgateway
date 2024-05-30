package uz.datalab.verifix.hikgateway.core;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.vhr.VHRClient;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Commands;
import uz.datalab.verifix.hikgateway.entity.Middleware;

@RequiredArgsConstructor
@Setter
@Component
@Scope("prototype")
public class DeviceWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DeviceWorker.class);
    private final MiddlewareEngine middlewareEngine;
    private final VHRClient vhrClient;
    private final CommandExecutor commandExecutor;

    private Middleware middleware;
    private long deviceId;

    @Override
    public void run() {
        try {
            while (true) {
                // load commands from VHR
                Commands commands = vhrClient.loadCommands(middleware, deviceId);

                if (commands.getCommands().isEmpty()) return;

                ////////////////////////////
                /// CONCURRENT EXECUTION ///
                ///        BEGIN         ///
                ////////////////////////////

                // run commands on Hik
//                CommandsResult commandsResult = commandExecutor.executeCommandsConcurrently(middleware, deviceId, commands);

                // send commands result to VHR
//                vhrClient.saveCommands(middleware, commandsResult);

                ////////////////////////////
                /// CONCURRENT EXECUTION ///
                ///         END          ///
                ////////////////////////////

                ////////////////////////////
                ///   LINEAR EXECUTION   ///
                ///        BEGIN         ///
                ////////////////////////////
                commandExecutor.executeCommands(middleware, deviceId, commands);
                ////////////////////////////
                ///   LINEAR EXECUTION   ///
                ///         END          ///
                ////////////////////////////
            }
        } catch (Exception e) {
            log.error("Error occurred while executing commands for device, middlewareId: {}, deviceId: {}", middleware.getId(), deviceId, e);
        } finally {
            middlewareEngine.removeDeviceWorker(middleware.getId(), deviceId);
        }
    }
}
