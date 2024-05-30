package uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Builder
@Getter
@Setter
public class HikDevice {
    @JsonProperty("protocolType")
    private String protocolType;
    @JsonProperty("EhomeParams")
    private EhomeParams ehomeParams;
    @JsonProperty("devName")
    private String deviceName;
    @JsonProperty("devType")
    private String deviceType;
}
