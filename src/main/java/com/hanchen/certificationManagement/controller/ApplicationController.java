package com.hanchen.certificationManagement.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanchen.certificationManagement.dao.ContractorDAO;
import com.hanchen.certificationManagement.entity.contract.Contractor;

@Controller
public class ApplicationController {
	@Autowired
	ContractorDAO contractorDAO;

	@PostMapping("/application_submit")
	public String applicationSubmit(@ModelAttribute Contractor applicant) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		applicant.setApplyTime(LocalDateTime.now().format(formatter));
		System.out.println(applicant);
		contractorDAO.add(applicant);
		return "application_result";
	}
}
