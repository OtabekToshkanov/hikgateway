package uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DeviceAddRequest {
    @JsonProperty("DeviceInList")
    private List<DeviceInListItem> deviceInList = new ArrayList<>();

    public void addDevice(DeviceInListItem deviceInListItem) {
        deviceInList.add(deviceInListItem);
    }
}
