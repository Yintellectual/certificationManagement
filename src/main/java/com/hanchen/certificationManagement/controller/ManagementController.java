package com.hanchen.certificationManagement.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanchen.certificationManagement.dao.ContractorDAO;
import com.hanchen.certificationManagement.entity.contract.Contractor;
import com.hanchen.certificationManagement.pdfEditor.PdfGenaratorUtil;

@Controller
public class ManagementController {
	@Autowired
	ContractorDAO contractorDAO;
	@Autowired
	PdfGenaratorUtil pdfGenaratorUtil;
	
	@GetMapping("/management/listAllApplication")
	@ResponseBody
	public Contractor[] listAllApplication() {
		System.out.println("show all");
		return contractorDAO.findAll();
	}
	
	@PostMapping("/management/approve")
	@ResponseBody
	public Contractor approve(@RequestBody String phone, HttpServletRequest request) {
		Contractor contractor = contractorDAO.find(phone);
		if(contractor==null){
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String handleTime = LocalDateTime.now().format(formatter); 
		String contractLink = "";
		try {
			URL serverURL = new URL(request.getRequestURL().toString());
			contractLink = serverURL.getHost()+":"+serverURL.getPort()+"/certification/"+phone+".pdf";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pdfGenaratorUtil.editPdf("sample.pdf", contractor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contractorDAO.approve(phone, handleTime, contractLink);
		contractor.setHandleTime(handleTime);
		contractor.setContractLink(contractLink);
		return contractor;
	}
	
	@PostMapping("/management/deny")
	@ResponseBody
	public Contractor deny(@RequestBody String phone) {
		Contractor contractor = contractorDAO.find(phone);
		if(contractor.getContractLink()==null||contractor.getContractLink().isEmpty()){
			contractorDAO.deny(phone);
		}
		return contractor;
	}
}
