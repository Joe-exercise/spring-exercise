package com.risknarrative.springexercise.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.risknarrative.springexercise.domain.TruProxyOfficerResponseDto;
import com.risknarrative.springexercise.domain.CompanyResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TruProxyRestClient {
  
  @Qualifier("truProxyWebClient")
  private final WebClient webClient;


  public ResponseEntity<CompanyResponseDto> getCompany(String searchCriteria, String apiKey) {

    log.info("Calling Company Search with criteria: {}", searchCriteria);

    return webClient.get()
              .uri(uriBuilder -> uriBuilder
                .path("/v1/Search")
                .queryParam("Query", searchCriteria)
                .build())
              .header("x-api-key", apiKey)
              .retrieve()
              .toEntity(CompanyResponseDto.class)
              .block();
  }

  public ResponseEntity<TruProxyOfficerResponseDto> getOfficers(String companyNumber, String apiKey) {

    log.info("Calling Officer Search with Company Number: {}", companyNumber);

    return webClient.get()
              .uri(uriBuilder -> uriBuilder
                .path("/v1/Officers")
                .queryParam("CompanyNumber", companyNumber)
                .build())
              .header("x-api-key", apiKey)
              .retrieve()
              .toEntity(TruProxyOfficerResponseDto.class)
              .block();
  }
}
