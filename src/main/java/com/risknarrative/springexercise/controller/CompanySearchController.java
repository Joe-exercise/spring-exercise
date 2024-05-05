package com.risknarrative.springexercise.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.risknarrative.springexercise.domain.CompanyResponseDto;
import com.risknarrative.springexercise.domain.CompanySearchRequestDto;
import com.risknarrative.springexercise.service.CompanySearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/com.risknarrative.springexercise/v1/companysearch")
@RequiredArgsConstructor
public class CompanySearchController implements CompanySearchApi {

  private final CompanySearchService companySearchService;
  
  @Override
  public ResponseEntity<CompanyResponseDto> search(String xApiKey, CompanySearchRequestDto request) {

    var companySearchResponseDto = companySearchService.search(request, xApiKey);
    return new ResponseEntity<>(companySearchResponseDto, HttpStatus.OK);
  }

}
