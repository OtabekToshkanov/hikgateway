package uz.datalab.verifix.hikgateway.client.hik.entity.devicedel.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Device {
    @JsonProperty("devIndex")
    private String deviceIndex;
    private String status;

    public boolean isSuccessful() {
        return "success".equals(status);
    }
}
