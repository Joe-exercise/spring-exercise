package com.risknarrative.springexercise.restclient;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class TruProxyWebConfig {
  
  @Value("${truproxy.base.url}")
  private String truProxyBaseUrl;

  @Bean
  public WebClient truProxyWebClient() throws SSLException {

    // Assumption for exercise.
    var sslContext = SslContextBuilder.forClient()
                          .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

    HttpClient httpClient = HttpClient.create()
                              .secure(spec -> spec.sslContext(sslContext));
                            
    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(truProxyBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }
}
