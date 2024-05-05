package com.risknarrative.springexercise.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.risknarrative.springexercise.domain.CompanyResponseDto;
import com.risknarrative.springexercise.domain.CompanySearchRequestDto;

/**
 * Public API for Company Search.
 */
public interface CompanySearchApi {
  
  @PostMapping("/Search")
  ResponseEntity<CompanyResponseDto> search(@RequestHeader("x-api-key") String xApiKey, @RequestBody CompanySearchRequestDto request);
  
}
