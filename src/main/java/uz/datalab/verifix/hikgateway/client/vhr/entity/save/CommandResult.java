package uz.datalab.verifix.hikgateway.client.vhr.entity.save;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class CommandResult {
    @JsonProperty("command_id")
    private final String commandId;
    @JsonProperty("command_result")
    private String commandResult;
    @JsonProperty("response_code")
    private int responseCode;

    @JsonIgnore
    public boolean isPossibleInternalOrDeviceBusyError() {
        return responseCode == 403 || responseCode == 503;
    }
}
