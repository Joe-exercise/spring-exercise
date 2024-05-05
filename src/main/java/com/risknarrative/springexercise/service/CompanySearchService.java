package com.risknarrative.springexercise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.risknarrative.springexercise.domain.CompanySearchRequestDto;
import com.risknarrative.springexercise.domain.CompanyResponseDto;
import com.risknarrative.springexercise.domain.Item;
import com.risknarrative.springexercise.domain.Officer;
import com.risknarrative.springexercise.restclient.TruProxyRestClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanySearchService {

  private final TruProxyRestClient truProxyRestClient;
  private static final String COMPANY_ACTIVE = "active";
  private final CompanyPersistence companyPersistence;


  public CompanyResponseDto search(CompanySearchRequestDto request, String xApiKey) {

    if (request.getCompanyNumber() == null && request.getCompanyName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company Name or Numnber must be supplied");
    }

    var searchCriteria = request.getCompanyName();
    if (request.getCompanyNumber() != null && !request.getCompanyNumber().isEmpty()) {
      searchCriteria = request.getCompanyNumber();
      var retrievedCompany = companyPersistence.getSavedCompany(searchCriteria);
      if (retrievedCompany != null) {
        return retrievedCompany;
      }
    }

    return retrieveCompanyFromProxy(searchCriteria, xApiKey, request.isActiveCompaniesOnly());

  }

  private CompanyResponseDto retrieveCompanyFromProxy(String searchCriteria, String xApiKey, boolean isActiveCompaniesOnly) {

    var response = truProxyRestClient.getCompany(searchCriteria, xApiKey);

    var companyResponse = response.getBody();

    if(isActiveCompaniesOnly) {
      companyResponse = filterActiveCompanies(companyResponse);
    }

    return retrieveOfficers(companyResponse, xApiKey);

  }

  private CompanyResponseDto filterActiveCompanies(CompanyResponseDto companyResponse) {

    List<Item> filteredCompanies = companyResponse.getItems().stream()
        .filter(comp -> COMPANY_ACTIVE.equalsIgnoreCase(comp.getCompany_status()))
        .collect(Collectors.toList());

    companyResponse.setItems(filteredCompanies);
    companyResponse.setTotal_results(filteredCompanies.size());
    return companyResponse;

  }

  private CompanyResponseDto retrieveOfficers(CompanyResponseDto response, String xApiKey) {

    List<Item> companiesWithOfficers = response.getItems().stream()
        .map(item -> retrieveOfficersForCompanyAndSaveCompany(item, xApiKey))
        .collect(Collectors.toList());

    var companyOfficerResponseDto = new CompanyResponseDto();
    companyOfficerResponseDto.setTotal_results(response.getTotal_results());
    companyOfficerResponseDto.setItems(companiesWithOfficers);
    return companyOfficerResponseDto;
  }

  /*
   * Consider mapping the Officer/Item classes.
   * CopyProperties used for brevity in exercise.
   */
  private Item retrieveOfficersForCompanyAndSaveCompany(Item company, String xApiKey) {
      
    var response = truProxyRestClient.getOfficers(company.getCompany_number(), xApiKey);

    if(response == null && response.getBody() == null && response.getBody().getItems() == null) {
      return company;
    }

    List<Officer> officers = response.getBody().getItems().stream()
                              .filter(item -> item.getResigned_on() == null)
                              .map(item -> { 
                                var officer = new Officer();
                                BeanUtils.copyProperties(item, officer);
                                return officer;
                              })
                              .collect(Collectors.toList());
    
    company.setOfficers(officers);
    companyPersistence.saveCompany(company);
    return company;
  }
}
