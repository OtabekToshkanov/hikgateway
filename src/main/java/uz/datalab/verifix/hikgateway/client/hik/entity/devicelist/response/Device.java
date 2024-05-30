package uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.response;

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
    @JsonProperty("EhomeParams")
    private EhomeParams ehomeParams;
    @JsonProperty("ISAPIPortBound")
    private boolean iSAPIPortBound;
    private boolean activeStatus;
    private String devIndex;
    private String devMode;
    private String devName;
    private String devStatus;
    private String devType;
    private String devVersion;
    private String protocolType;
    private int videoChannelNum;
}
