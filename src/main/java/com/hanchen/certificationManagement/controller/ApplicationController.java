package com.hanchen.certificationManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanchen.certificationManagement.entity.contract.Contractor;

@Controller
public class ApplicationController {
	
	  @PostMapping("/application_submit")
	  public String applicationSubmit(@ModelAttribute Contractor applicant) {
		  System.out.println(applicant);
		  return "application_result";
	  }
}
