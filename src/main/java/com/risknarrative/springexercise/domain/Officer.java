package com.risknarrative.springexercise.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Officer {

  private String name;
  private String officer_role;
  private LocalDate appointed_on;
  private Address address;

}
