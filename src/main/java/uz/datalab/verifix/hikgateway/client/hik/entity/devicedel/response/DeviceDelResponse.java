package uz.datalab.verifix.hikgateway.client.hik.entity.devicedel.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDelResponse {
   @JsonProperty("DelDevList")
   private List<DeviceDelListItem> delListItems;
}
