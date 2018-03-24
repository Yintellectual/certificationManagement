package com.hanchen.certificationManagement.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanchen.certificationManagement.dao.ContractorDAO;
import com.hanchen.certificationManagement.entity.contract.Contractor;
import com.hanchen.certificationManagement.pdfEditor.PdfGenaratorUtil;


@Controller
public class PdfController {

	@Autowired
	PdfGenaratorUtil pdfGenaratorUtil;

	@Autowired
	ContractorDAO contractorDao;
	@Autowired
	ApplicationController applicationController;
	
	@RequestMapping(value = "/new_contractor", method = RequestMethod.POST)
	@ResponseBody
	public Contractor process(@RequestBody Contractor contractor, HttpServletRequest request) throws Exception {
		System.out.println(contractor);
		String phone = contractor.getPhone();
		Contractor contractorInDatabase = contractorDao.find(phone);
		if(contractorInDatabase==null){
			applicationController.applicationSubmit(contractor);
			contractorInDatabase = contractorDao.find(phone);
		}
		contractorInDatabase.setName(contractor.getName());
		contractorInDatabase.setWechat(contractor.getWechat());
		contractorInDatabase.setShouquanJibie(contractor.getShouquanJibie());
		contractor = contractorInDatabase;
		String contractLink = "";
		try {
			URL serverURL = new URL(request.getRequestURL().toString());
			contractLink = "http://"+serverURL.getHost()+":"+serverURL.getPort()+"/certification/"+phone+".pdf";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contractor.setContractLink(contractLink);
		System.out.println(contractor);
		Contractor result =contractorDao.approve(contractor); 
		pdfGenaratorUtil.editPdf("aizhitang.pdf", result);
		return result;
	}

	@RequestMapping(value = "/certification/{phone}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getPDF(@PathVariable(value = "phone") String phone) throws IOException {
		// retrieve contents of "C:/tmp/report.pdf" that were written in
		// showHelp
		System.out.println("load pdf");
		Path pdfPath = Paths.get(".", "temp", phone);
		System.out.println("pdf"+pdfPath.toString());
		byte[] contents = Files.readAllBytes(pdfPath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		String filename = phone;
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
		System.out.println("pdf sent");
		return response;
	}
}
