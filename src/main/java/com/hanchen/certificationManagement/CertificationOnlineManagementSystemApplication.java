package com.hanchen.certificationManagement;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.hanchen.certificationManagement.dao.ContractorDAO;
import com.hanchen.certificationManagement.entity.contract.Contractor;

@SpringBootApplication
public class CertificationOnlineManagementSystemApplication {
	@Autowired
	ContractorDAO contractorDAO;

	public static void main(String[] args) {
		SpringApplication.run(CertificationOnlineManagementSystemApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {
		return new CommandLineRunner() {

			@Override
			public void run(String... args) throws Exception {
				// TODO Auto-generated method stub
				Contractor[] array = new Contractor[3];
				array[0] = new Contractor("11", "12", "13");
				array[1] = new Contractor("21", "22", "23");
				array[2] = new Contractor("31", "32", "33");
				contractorDAO.add(array[0]);
				contractorDAO.add(array[1]);
				contractorDAO.add(array[2]);
				System.out.println(Arrays.toString(contractorDAO.findAll()));
			}
		};
	}
}
