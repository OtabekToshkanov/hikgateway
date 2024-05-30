package uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceListRequest {
    @JsonProperty("SearchDescription")
    private SearchDescription searchDescription;

    public DeviceListRequest(int position) {
        this.searchDescription = new SearchDescription(position);
    }
}
