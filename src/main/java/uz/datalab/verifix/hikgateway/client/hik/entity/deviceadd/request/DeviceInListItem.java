package uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DeviceInListItem {
    @JsonProperty("Device")
    private HikDevice hikDevice;
}
