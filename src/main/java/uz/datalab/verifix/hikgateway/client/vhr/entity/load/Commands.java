package uz.datalab.verifix.hikgateway.client.vhr.entity.load;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Commands {
    @JsonProperty("commands_list")
    private List<Command> commands;
    @JsonProperty("operation_mode")
    private String operationMode;

    public Commands() {
        commands = new ArrayList<>();
    }
}
