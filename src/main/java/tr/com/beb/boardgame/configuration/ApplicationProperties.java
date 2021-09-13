package tr.com.beb.boardgame.configuration;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class ApplicationProperties {

    @NotBlank
    private String tokenSecretKey;

    @NotBlank
    private String webSocketServerAddress;

    public String getTokenSecretKey() {
        return tokenSecretKey;
    }

    public void setTokenSecretKey(String tokenSecretKey) {
        this.tokenSecretKey = tokenSecretKey;
    }

    public String getWebSocketServerAddress() {
        return webSocketServerAddress;
    }

    public void setWebSocketServerAddress(String webSocketServerAddress) {
        this.webSocketServerAddress = webSocketServerAddress;
    }

}
