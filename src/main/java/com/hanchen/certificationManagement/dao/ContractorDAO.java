package com.hanchen.certificationManagement.dao;

import com.hanchen.certificationManagement.entity.contract.Contractor;

public interface ContractorDAO {
	Contractor find(String phone);
	Contractor[] findAll();
	Contractor approve(Contractor contractor);
	void add(Contractor newContractor);
	void deny(String phone);
	long countApprovedContractors();
	long countApplicants();
}
