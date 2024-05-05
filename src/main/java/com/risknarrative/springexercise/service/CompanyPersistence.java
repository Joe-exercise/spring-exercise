package com.risknarrative.springexercise.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.risknarrative.springexercise.db.CompanySearchDbo;
import com.risknarrative.springexercise.db.CompanySearchRepository;
import com.risknarrative.springexercise.domain.CompanyResponseDto;
import com.risknarrative.springexercise.domain.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompanyPersistence {

  private final CompanySearchRepository companySearchRepository;
  private final ObjectMapper jsonMapper = JsonMapper.builder()
                                            .build() 
                                            .registerModule(
                                              new JavaTimeModule().addSerializer(
                                                new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)
                                              ) 
                                            );

  public void saveCompany(Item company) {
    var companySearchDbo = new CompanySearchDbo();
    companySearchDbo.setCompanyNumber(company.getCompany_number());
    try {
      companySearchDbo.setJsonString(jsonMapper.writeValueAsString(company));
    } catch (JsonProcessingException e) {
      // Exercise assumption - ignore and continue
      log.error("Json mapping failed: {}", company.toString());
      return;
    }

    try {
      companySearchRepository.save(companySearchDbo);
    } catch (DataIntegrityViolationException e) {
      // Assumption - ignoring duplicate insert attempts for exercise
      log.debug("Duplicate found, skipping");
    }  
  }

  public CompanyResponseDto getSavedCompany(String companyNumber) {
    var companyDbo = companySearchRepository.findByCompanyNumber(companyNumber);
    if (companyDbo == null) {
      return null;
    }

    var company = new Item();
    try {
      company = jsonMapper.readValue(companyDbo.getJsonString(), Item.class);
    } catch (JsonProcessingException e) {
      // Exercise assumption - ignore and continue
      log.error("Json mapping failed: {}", companyDbo.toString());
      return null;
    }
    
    var companyResponseDto = new CompanyResponseDto();
    companyResponseDto.setTotal_results(1);
    companyResponseDto.setItems(List.of(company));

    return companyResponseDto;
  }

}
