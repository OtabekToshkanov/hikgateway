package uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.response;

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
    @JsonProperty("status")
    private String status;
    @JsonProperty("devIndex")
    private String deviceIndex;
    @JsonProperty("devName")
    private String deviceName;
    @JsonProperty("protocolType")
    private String protocolType;
    @JsonProperty("EhomeParams")
    private EhomeParams ehomeParams;
    @JsonProperty("subStatusCode")
    private String subStatusCode;

    public boolean isSuccessful() {
        return "success".equals(status);
    }
}
