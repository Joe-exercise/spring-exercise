package com.risknarrative.springexercise.domain;

import lombok.Data;
import java.util.List;

@Data
public class CompanyResponseDto {
  
  private int total_results;
  private List<Item> items;
}
