package com.hanchen.certificationManagement.dao;

import com.hanchen.certificationManagement.entity.contract.Contractor;

public interface ContractorDAO {
	Contractor find(String phone);
	Contractor[] findAll();
	void approve(String phone, String handleTime, String contractLink);
	void add(Contractor newContractor);
	void deny(String phone);
}
