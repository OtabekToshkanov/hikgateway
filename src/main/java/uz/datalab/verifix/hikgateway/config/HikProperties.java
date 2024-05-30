package uz.datalab.verifix.hikgateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "hik")
public class HikProperties {
    private String serverUrl;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
    private String username;
    private String password;

    public String getUrl(String uri) {
        return serverUrl + uri;
    }

    public String deviceListUrl() {
        return serverUrl + "/ISAPI/ContentMgmt/DeviceMgmt/deviceList?format=json";
    }
}
