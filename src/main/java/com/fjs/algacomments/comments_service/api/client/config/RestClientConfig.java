package com.fjs.algacomments.comments_service.api.client.config;

import com.fjs.algacomments.comments_service.api.client.ModerationServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public ModerationServiceClient sensorMonitoringClient(RestClientFactory factory){
        RestClient restClient = factory.temperatureMonitoringClient();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(ModerationServiceClient.class);
    }

}
