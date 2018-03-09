package com.hanchen.certificationManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanchen.certificationManagement.entity.contract.Contractor;

@Controller
public class ManagementController {
	  @GetMapping("/management/listAllApplication")
	  @ResponseBody
	  public Contractor[] listAllApplication() {
		  System.out.println("show all");
		  Contractor[] array = new Contractor[3];
		  array[0] = new Contractor("11","12", "13");
		  array[1] = new Contractor("21","22", "23");
		  array[2] = new Contractor("31","32", "33");
		  return array;
	  }
}
