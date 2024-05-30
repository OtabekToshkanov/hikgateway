package uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.response;

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
public class SearchResult {
    @JsonProperty("MatchList")
    private List<DeviceWrapper> matchList;
    private int numOfMatches;
    private int totalMatches;
}
