package com.risknarrative.springexercise;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.risknarrative.springexercise.domain.CompanyResponseDto;
import com.risknarrative.springexercise.domain.Item;
import com.risknarrative.springexercise.service.CompanyPersistence;

import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("test")
class CompanyPersistenceTest {

  @Autowired
  private CompanyPersistence companyPersistence;

  private final ObjectMapper jsonMapper = JsonMapper.builder()
                                            .build() 
                                            .registerModule(
                                              new JavaTimeModule().addSerializer(
                                                new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)
                                              ) 
                                            );

  @Test
  void insertAndRetrieveTest() throws Exception {
    
    var jsonCompanyStr = TestUtil.getStringFromFile("ResponseSingleCompany.json");
    var company = jsonMapper.readValue(jsonCompanyStr, CompanyResponseDto.class);

    companyPersistence.saveCompany(company.getItems().get(0));

    // Retrieve company
    var companyResponseDto = companyPersistence.getSavedCompany("14575777");

    assertThat(companyResponseDto).isNotNull();
    assertThat(companyResponseDto.getItems()).isNotEmpty();

    JSONAssert.assertEquals(jsonCompanyStr, jsonMapper.writeValueAsString(companyResponseDto), JSONCompareMode.LENIENT);
  }

  @Test
  void failRetrieveTest() throws Exception {

    // Retrieve non-existant company
    var companyResponseDto = companyPersistence.getSavedCompany("NONE_EXISTANT");

    assertThat(companyResponseDto).isNull();
  }

  @Test
  void insertDuplicateTest() throws Exception {
    
    var jsonCompanyStr = TestUtil.getStringFromFile("ResponseSingleCompany.json");
    var company = jsonMapper.readValue(jsonCompanyStr, CompanyResponseDto.class);

    companyPersistence.saveCompany(company.getItems().get(0));

    // Save company with duplicate company number
    var duplicateCompany = new Item();
    duplicateCompany.setCompany_number("14575777");
    companyPersistence.saveCompany(duplicateCompany);

    // Retrieve company
    var companyResponseDto = companyPersistence.getSavedCompany("14575777");

    assertThat(companyResponseDto).isNotNull();
    assertThat(companyResponseDto.getItems()).isNotEmpty();

    JSONAssert.assertEquals(jsonCompanyStr, jsonMapper.writeValueAsString(companyResponseDto), JSONCompareMode.LENIENT);
  }  
}
