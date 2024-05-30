package uz.datalab.verifix.hikgateway.client.hik.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class HikErrorResponse {
    private long errorCode;
    private String errorMsg;
    private long statusCode;
    private String statusString;
    private String subStatusCode;
}
