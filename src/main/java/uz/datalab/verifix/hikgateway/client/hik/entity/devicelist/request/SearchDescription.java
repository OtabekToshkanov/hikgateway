package uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SearchDescription {
    private int position;
    private int maxResult;
    @JsonProperty("Filter")
    private Filter filter;

    public SearchDescription(int position) {
        this.position = position;
        this.maxResult = 50;
        this.filter = new Filter();
    }

    public SearchDescription() {
        this(0);
    }
}
