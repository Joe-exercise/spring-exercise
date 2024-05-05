package com.risknarrative.springexercise.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TruProxyOfficerItem {

  private String name;
  private String officer_role;
  private LocalDate appointed_on;
  private LocalDate resigned_on;
  private Address address;
  
}
