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
public class EhomeParams {
    @JsonProperty("EhomeID")
    private String ehomeID;
}
