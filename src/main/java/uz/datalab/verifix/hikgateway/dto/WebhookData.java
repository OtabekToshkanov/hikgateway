package uz.datalab.verifix.hikgateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WebhookData {
    private long id;
    @JsonProperty("device_id")
    private long deviceId;

    @Override
    public String toString() {
        return "WebhookData{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                '}';
    }
}