package com.risknarrative.springexercise.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Item {

  private String company_number;
  private String company_type;
  private String company_status;
  private String title;
  private LocalDate date_of_creation;
  private Address address;
  private List<Officer> officers;

  
}
