package by.zemich.binancebot.config;

import by.zemich.binancebot.config.properties.PrivateProperties;
import com.binance.connector.client.WebSocketApiClient;
import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebSocketApiClientImpl;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.binance.connector.client.utils.WebSocketConnection;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import com.binance.connector.client.utils.signaturegenerator.SignatureGenerator;
import com.binance.connector.client.utils.websocketapi.WebSocketApiRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BinanceConfig {

    private final PrivateProperties privateProperties;

    public BinanceConfig(PrivateProperties privateProperties) {
        this.privateProperties = privateProperties;
    }

    @Bean
    SpotClientImpl spotClient() {
        return new SpotClientImpl(privateProperties.getApiKey(), privateProperties.getSecretKey());
    }

    @Bean
    WebSocketApiClient webSocketApiClient(HmacSignatureGenerator signatureGenerator){
        return new WebSocketApiClientImpl(privateProperties.getApiKey(), signatureGenerator);
    }

    @Bean
    WebSocketStreamClient webSocketStreamClient(){
        return new WebSocketStreamClientImpl();
    }

    @Bean
    HmacSignatureGenerator signatureGenerator(){
        return new HmacSignatureGenerator(privateProperties.getSecretKey());
    }

}
