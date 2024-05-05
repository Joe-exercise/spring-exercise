package com.risknarrative.springexercise.domain;

import lombok.Data;
import java.util.List;

@Data
public class TruProxyOfficerResponseDto {
  
  private int total_results;
  private List<TruProxyOfficerItem> items;
}
