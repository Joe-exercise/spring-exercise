package com.risknarrative.springexercise.domain;

import lombok.Data;

@Data
public class CompanySearchRequestDto {
  
  private String companyName;
  private String companyNumber;
  private boolean activeCompaniesOnly;
}
