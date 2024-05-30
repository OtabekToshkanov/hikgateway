package uz.datalab.verifix.hikgateway.client.hik;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uz.datalab.verifix.hikgateway.client.hik.entity.HikResult;
import uz.datalab.verifix.hikgateway.client.hik.entity.HikUtil;
import uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.request.DeviceListRequest;
import uz.datalab.verifix.hikgateway.client.hik.entity.devicelist.response.DeviceListResponse;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.FaceDataRecord;
import uz.datalab.verifix.hikgateway.client.vhr.entity.load.Photo;
import uz.datalab.verifix.hikgateway.config.HikProperties;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class HikClient {
    private static final Logger log = LoggerFactory.getLogger(HikClient.class);
    private final MediaType JSON = MediaType.get("application/json");
    private final OkHttpClient hikHttpClient;
    private final HikProperties properties;
    private final ObjectMapper objectMapper;

    private RequestBody createRequestBody(Object body) throws JsonProcessingException {
        return RequestBody.create(objectMapper.writeValueAsString(body), JSON);
    }

    public HikResult makeSimpleRequest(String uri, String method, Object requestBody) {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(properties.getUrl(uri));

            switch (method) {
                case "POST" -> requestBuilder.post(createRequestBody(requestBody));
                case "PUT" -> requestBuilder.put(createRequestBody(requestBody));
                case "DELETE" -> requestBuilder.delete(createRequestBody(requestBody));
                case "GET" -> requestBuilder.get();
                default -> {
                    log.error("Unsupported HTTP method: {}", method);
                    return new HikResult(500, makeErrorResponse("Unsupported HTTP method", "unsupportedMethod"));
                }
            }

            try (Response response = hikHttpClient.newCall(requestBuilder.build()).execute()) {
                assert response.body() != null;
                String responseBody = response.body().string();

                return new HikResult(response.code(), responseBody);
            } catch (IOException e) {
                log.error("Error occurred while making request to HikVision gateway", e);
                return new HikResult(500, makeErrorResponse("HikGateway connection error", "connectionFailed"));
            }
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serializing request body to JSON", e);
            return new HikResult(500, makeErrorResponse("Serialization error", "serializationFailed"));
        } catch (Exception e) {
            log.error("Unexpected error occurred while making request to HikVision gateway", e);
            return new HikResult(500, makeErrorResponse("Unexpected HikGateway error", "hikGatewayFailed"));
        }
    }

    public HikResult makeSetPhotoRequest(String uri, FaceDataRecord faceDataRecord, Photo photo) {
        try {
            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("FaceDataRecord", objectMapper.writeValueAsString(faceDataRecord))
                    .addFormDataPart("FaceImage", photo.fileName(), RequestBody.create(photo.bytes(), MediaType.get("image/jpeg")))
                    .build();

            Request request = new Request.Builder()
                    .url(properties.getUrl(uri))
                    .put(body)
                    .build();

            try (Response response = hikHttpClient.newCall(request).execute()) {
                assert response.body() != null;
                String responseBody = response.body().string();

                return new HikResult(response.code(), responseBody);
            } catch (IOException e) {
                log.error("Error occurred while making set photo request to HikVision gateway", e);
                return new HikResult(500, makeErrorResponse("HikGateway connection error", "connectionFailed"));
            }
        } catch (Exception e) {
            log.error("Error occurred while making set photo request to HikVision gateway", e);
            return new HikResult(500, makeErrorResponse("Unexpected HikGateway error", "hikGatewayFailed"));
        }
    }

    public DeviceListResponse makeDeviceListRequest(int position) {
        try {
            Request requestBuilder = new Request.Builder()
                    .url(properties.deviceListUrl())
                    .post(createRequestBody(new DeviceListRequest(position)))
                    .build();

            try (Response response = hikHttpClient.newCall(requestBuilder).execute()) {
                assert response.body() != null;
                return objectMapper.readValue(response.body().string(), DeviceListResponse.class);
            }
        } catch (Exception e) {
            log.error("Error occurred while making device list request to HikVision gateway", e);
            return null;
        }
    }

    private String makeErrorResponse(String errorMsg, String subStatusCode) {
        try {
            return objectMapper.writeValueAsString(HikUtil.makeErrorResponse(errorMsg, subStatusCode));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
