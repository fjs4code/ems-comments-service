package com.fjs.algacomments.comments_service.api.client.config;

import com.fjs.algacomments.comments_service.api.exception.ModerationServiceClientBadGatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final RestClient.Builder builder;

    public RestClient temperatureMonitoringClient(){

        this.builder
                .baseUrl("http://localhost:8081")
                .requestFactory(generateSimpleClientFactory())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ModerationServiceClientBadGatewayException();
                })
                .build();


        return builder.build();
    }

    private ClientHttpRequestFactory generateSimpleClientFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setReadTimeout(Duration.ofSeconds(5));
        factory.setConnectTimeout(Duration.ofSeconds(3));

        return factory;
    }

}
