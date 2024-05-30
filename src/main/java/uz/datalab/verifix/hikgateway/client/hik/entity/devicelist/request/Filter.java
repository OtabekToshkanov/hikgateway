package uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Filter {
    private String devType;
    private String[] protocolType;
    private String[] devStatus;
    @JsonProperty("ISAPIPortBound")
    private boolean[] iSAPIPortBound;

    public Filter() {
        devType = "AccessControl";
        protocolType = new String[]{"ehomeV5", "ISAPI"};
        devStatus = new String[]{"online", "offline", "sleep"};
        iSAPIPortBound = new boolean[]{true, false};
    }
}
