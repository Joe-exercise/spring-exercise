package com.risknarrative.springexercise.db;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CompanySearchDbo {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  
  @Column(unique=true)
  private String companyNumber;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private String jsonString;
}
