package uz.datalab.verifix.hikgateway.client.vhr.entity.load;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Command {
    @JsonProperty("command_id")
    private String commandId;
    @JsonProperty("command_code")
    private String commandCode;
    @JsonProperty("execution_mode")
    private String executionMode;
    @JsonProperty("command_route")
    private String commandRoute;
    @JsonProperty("http_method")
    private String httpMethod;
    @JsonProperty("command_body")
    private Object commandBody;
}