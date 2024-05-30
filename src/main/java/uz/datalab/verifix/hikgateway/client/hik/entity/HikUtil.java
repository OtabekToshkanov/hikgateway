package uz.datalab.verifix.hikgateway.client.hik.entity;

public class HikUtil {
    public static HikErrorResponse makeErrorResponse(String errorMsg, String subStatusCode) {
        return HikErrorResponse.builder()
                .errorCode(0)
                .errorMsg(errorMsg)
                .statusCode(8) // 8 will not be returned by HikVision API, so we use it to refer to an unknown error
                .statusString("HikGateway internal error")
                .subStatusCode(subStatusCode)
                .build();
    }
}