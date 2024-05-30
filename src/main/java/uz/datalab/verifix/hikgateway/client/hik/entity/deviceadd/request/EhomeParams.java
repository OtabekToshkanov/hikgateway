package uz.datalab.verifix.hikgateway.client.hik.entity.deviceadd.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Builder
@Getter
@Setter
public class EhomeParams {
    @JsonProperty("EhomeID")
    private String ehomeID;
    @JsonProperty("EhomeKey")
    private String ehomeKey;
}
