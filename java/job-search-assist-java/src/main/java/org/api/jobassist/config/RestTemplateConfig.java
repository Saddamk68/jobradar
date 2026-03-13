package org.api.jobassist.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Default RestTemplate — used for all general HTTP calls (short timeout)
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return buildRestTemplate(5, 10);
    }

    // Dedicated RestTemplate for Python batch — longer read timeout
    @Bean
    @Qualifier("pythonRestTemplate")
    public RestTemplate pythonRestTemplate() {
        return buildRestTemplate(5, 120); // 2 min read timeout for batch
    }

    private RestTemplate buildRestTemplate(int connectSeconds, int responseSeconds) {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(10);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(connectSeconds))
                .setResponseTimeout(Timeout.ofSeconds(responseSeconds))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

}
