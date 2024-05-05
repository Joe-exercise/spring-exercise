package com.risknarrative.springexercise.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySearchRepository extends JpaRepository<CompanySearchDbo, Long> {

  CompanySearchDbo findByCompanyNumber(String companyNumber);
  
}
