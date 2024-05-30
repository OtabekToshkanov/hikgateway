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
public class DeviceWrapper {
    @JsonProperty("Device")
    private Device device;
}
