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
			}
		};
	}
}
