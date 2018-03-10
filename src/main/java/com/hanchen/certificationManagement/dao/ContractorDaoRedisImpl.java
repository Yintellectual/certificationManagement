package com.hanchen.certificationManagement.dao;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hanchen.certificationManagement.entity.contract.Contractor;

@Service
public class ContractorDaoRedisImpl implements ContractorDAO {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private SetOperations<String, String> setOperations;
	private HashOperations<String, String, String> hashOperations;
	private ValueOperations<String, String> valueOperations;

	@PostConstruct
	public void init() {
		setOperations = stringRedisTemplate.opsForSet();
		hashOperations = stringRedisTemplate.opsForHash();
		valueOperations = stringRedisTemplate.opsForValue();
	}

	@Override
	public Contractor[] findAll() {
		// TODO Auto-generated method stub
		Set<String> members = setOperations.members("cm:contractors");
		List<Contractor> contractors = new ArrayList<>();
		for (String member : members) {
			contractors.add(find(member));
		}
		return contractors.toArray(new Contractor[0]);
	}

	@Override
	public void approve(String phone, String handleTime, String contractLink) {
		// TODO Auto-generated method stub
		if(phone==null||handleTime==null||contractLink==null||phone.isEmpty()||handleTime.isEmpty()||contractLink.isEmpty()){
			return;
		}
		String contractorKey = "cm:contractor:phone:" + phone;
		hashOperations.putIfAbsent(contractorKey, "handleTime", handleTime);
		hashOperations.putIfAbsent(contractorKey, "contractLink", contractLink);
	}

	@Override
	public void add(Contractor newContractor) {
		if(newContractor==null||newContractor.getPhone()==null||newContractor.getPhone().isEmpty()){
			return;
		}
		// TODO Auto-generated method stub
		setOperations.add("cm:contractors", newContractor.getPhone());
		String contractorKey = "cm:contractor:phone:" + newContractor.getPhone();
		if (newContractor.getName() != null)
			hashOperations.putIfAbsent(contractorKey, "name", newContractor.getName());
		if (newContractor.getPhone() != null)
			hashOperations.putIfAbsent(contractorKey, "phone", newContractor.getPhone());
		if (newContractor.getWechat() != null)
			hashOperations.putIfAbsent(contractorKey, "wechat", newContractor.getWechat());
		if (newContractor.getApplyTime() != null)
			hashOperations.putIfAbsent(contractorKey, "applyTime", newContractor.getApplyTime());
		if (newContractor.getHandleTime() != null)
			hashOperations.putIfAbsent(contractorKey, "handleTime", newContractor.getHandleTime());
		if (newContractor.getContractLink() != null)
			hashOperations.putIfAbsent(contractorKey, "contractLink", newContractor.getContractLink());
		if (newContractor.getMessage() != null)
			hashOperations.putIfAbsent(contractorKey, "message", newContractor.getMessage());
	}

	@Override
	public void deny(String phone) {
		// TODO Auto-generated method stub
		if(phone==null||phone.isEmpty()){
			return;
		}
		String contractorKey = "cm:contractor:phone:" + phone;
		setOperations.remove("cm:contractors", phone);
		stringRedisTemplate.delete(contractorKey);
	}

	@Override
	public Contractor find(String phone) {
		// TODO Auto-generated method stub
		if(phone==null||phone.isEmpty()){
			return null;
		}
		String contractorKey = "cm:contractor:phone:" + phone;
		String name = hashOperations.get(contractorKey, "name");
		String wechat = hashOperations.get(contractorKey, "phone");
		String applyTime = hashOperations.get(contractorKey, "applyTime");
		String handleTime = hashOperations.get(contractorKey, "handleTime");
		String contractLink = hashOperations.get(contractorKey, "contractLink");
		String message = hashOperations.get(contractorKey, "message");
		Contractor contractor = new Contractor(name, phone, wechat, applyTime, handleTime, contractLink, message);
		return contractor;
	}

}
