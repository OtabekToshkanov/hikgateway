package uz.datalab.verifix.hikgateway.client.vhr.entity.load;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Commands {
    @JsonProperty("commands_list")
    private List<Command> commands;
    @JsonProperty("operation_mode")
    private String operationMode;
    @JsonProperty("delay_attempt_times")
    private int[] delayAttemptTimes;
    @JsonProperty("next_load_delay")
    private int nextLoadDelay;
}
