package com.hanchen.certificationManagement.dao;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private ListOperations<String, String> listOperations;
	private ZSetOperations<String, String> zetOperations;
	private MembersPhonebook membersPhonebook;
	@PostConstruct
	public void init() {
		setOperations = stringRedisTemplate.opsForSet();
		hashOperations = stringRedisTemplate.opsForHash();
		valueOperations = stringRedisTemplate.opsForValue();
		listOperations = stringRedisTemplate.opsForList();
		zetOperations = stringRedisTemplate.opsForZSet();
		membersPhonebook = new MembersPhonebook();
	}

	
	//store phone numbers of the applicants in a zet, and the approved in a list 
	class MembersPhonebook{
		private String applicantsZetKey = "cm:applicants";
		private String approvedListKey = "cm:approved";
		private String membersSet = "cm:members";
		private long newScore = getGreatestScore()+1;
		//set the new value with greatest score
		public void addApplicant(String phone){
			if(setOperations.isMember(membersSet, phone)){
				//the new phone belongs to an applicant
				if(zetOperations.score(applicantsZetKey, phone) !=null){
					zetOperations.add(applicantsZetKey, phone ,newScore);
				}
				//the new phone belongs to an approved
				else{
					
				}
				
			}
			//the new phone belongs to a new member
			else{
				zetOperations.add(applicantsZetKey, phone ,newScore);
				setOperations.add(membersSet, phone);
			}
		}
		private long getGreatestScore() throws RuntimeException{
			Set<TypedTuple<String>> latestTuple = zetOperations.reverseRangeWithScores(applicantsZetKey, 0, 0);
			if(latestTuple == null || latestTuple.isEmpty()){
				return 0;
			}else{
				for(TypedTuple<String> tuple:latestTuple){
					return tuple.getScore().longValue();
				}
			}
			//error code
			return -1;
		}
		public void approve(String phone){
			if(setOperations.isMember(membersSet, phone)){
				//the phone belongs to an applicant
				if(zetOperations.score(applicantsZetKey, phone) !=null){
					zetOperations.remove(applicantsZetKey, phone);
					listOperations.leftPush(approvedListKey, phone);
				}
				//the phone belongs to an approved
				else{
					
				}
				
			}
			//the phone belongs to a new member
			else{
				
			}
		}
		public void deny(String phone){
			if(setOperations.isMember(membersSet, phone)){
				//the phone belongs to an applicant
				if(zetOperations.score(applicantsZetKey, phone) !=null){
					zetOperations.remove(applicantsZetKey, phone);
				}
				//the phone belongs to an approved
				else{
					
				}
				
			}
			//the phone belongs to a new member
			else{
				setOperations.remove(membersSet, phone);
			}
		}
		public List<String> members(){
			List<String> applicants = applicants();
			List<String> approved = approved();
			List<String> members = null;
			if (applicants != null) {
				applicants.addAll(approved);
				members = applicants;
			}else{
				members = approved;
			}
			return members;
		}
		public List<String> applicants(){
			return new ArrayList<String>(zetOperations.reverseRange(applicantsZetKey, 0, -1));
		}
		public List<String> approved(){
			return listOperations.range(approvedListKey, 0, -1);
		}
		public boolean isMember(String phone){
			return setOperations.isMember(membersSet, phone);
		}
		public boolean isApplicant(String phone){
			return setOperations.isMember(membersSet, phone) && zetOperations.score(applicantsZetKey, phone) !=null;
		}
		public boolean isApproved(String phone){
			return setOperations.isMember(membersSet, phone) && !isApplicant(phone);
		}
		public long countApplicants(){
			return zetOperations.count(applicantsZetKey, Double.MIN_VALUE, Double.MAX_VALUE);
		}
		public long countApproved(){
			return listOperations.size(approvedListKey);
		}
	}
	
	
	@Override
	public Contractor[] findAll() {
		List<Contractor> contractors = new ArrayList<>();
		for (String member : membersPhonebook.members()) {
			contractors.add(find(member));
		}
		System.out.println(contractors);
		return contractors.toArray(new Contractor[0]);
	}

	@Override
	public Contractor approve(Contractor contractor) {
		// TODO Auto-generated method stub
		if (contractor.getPhone() == null || contractor.getPhone().isEmpty()) {
			return null;
		}
		if (contractor.getName() == null || contractor.getName().isEmpty()) {
			return null;
		}
		if (contractor.getWechat() == null || contractor.getWechat().isEmpty()) {
			return null;
		}
		if (contractor.getContractLink() == null || contractor.getContractLink().isEmpty()) {
			return null;
		}
		if (contractor.getShouquanJibie() == null || contractor.getShouquanJibie().isEmpty()) {
			return null;
		}
		String phone = contractor.getPhone();
		
		membersPhonebook.approve(phone);

		String contractorKey = "cm:contractor:phone:" + phone;
		// name
		hashOperations.put(contractorKey, "name", contractor.getName());
		// weChat
		hashOperations.put(contractorKey, "wechat", contractor.getWechat());
		// handleTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String handleTime = LocalDateTime.now().format(formatter);
		hashOperations.putIfAbsent(contractorKey, "handleTime", handleTime);
		hashOperations.putIfAbsent(contractorKey, "contractLink", contractor.getContractLink());
		hashOperations.putIfAbsent(contractorKey, "shouquanJibie", contractor.getShouquanJibie());
		hashOperations.putIfAbsent(contractorKey, "shouquanBianma",
				String.format("AZT%04d", countApprovedContractors() + 1));
		hashOperations.putIfAbsent(contractorKey, "shouquanRiqi", LocalDate.now().toString());
		Contractor result = find(phone);
		System.out.println(result);
		return result;
	}

	@Override
	public void add(Contractor newContractor) {
		if (newContractor == null || newContractor.getPhone() == null || newContractor.getPhone().isEmpty()) {
			return;
		}
		String phone = newContractor.getPhone();
		membersPhonebook.addApplicant(phone);
		if(membersPhonebook.isApproved(phone)){
			
		}
		//applicant or new
		else{
			String contractorKey = "cm:contractor:phone:" + newContractor.getPhone();
			if (newContractor.getName() != null)
				hashOperations.put(contractorKey, "name", newContractor.getName());
			if (newContractor.getPhone() != null)
				hashOperations.putIfAbsent(contractorKey, "phone", newContractor.getPhone());
			if (newContractor.getWechat() != null)
				hashOperations.put(contractorKey, "wechat", newContractor.getWechat());
			if (newContractor.getApplyTime() != null)
				hashOperations.put(contractorKey, "applyTime", newContractor.getApplyTime());
			if (newContractor.getHandleTime() != null)
				hashOperations.put(contractorKey, "handleTime", newContractor.getHandleTime());
			if (newContractor.getContractLink() != null)
				hashOperations.put(contractorKey, "contractLink", newContractor.getContractLink());
			if (newContractor.getContractLink() != null)
				hashOperations.put(contractorKey, "shouquanJibie", newContractor.getShouquanJibie());
			if (newContractor.getContractLink() != null)
				hashOperations.put(contractorKey, "shouquanBianma", newContractor.getShouquanBianma());
			if (newContractor.getContractLink() != null)
				hashOperations.put(contractorKey, "shouquanRiqi", newContractor.getShouquanRiqi());
			if (newContractor.getMessage() != null)
				hashOperations.put(contractorKey, "message", newContractor.getMessage());
		}
	}

	@Override
	public void deny(String phone) {
		// TODO Auto-generated method stub
		if (phone == null || phone.isEmpty()) {
			return;
		}
		membersPhonebook.deny(phone);
		String contractorKey = "cm:contractor:phone:" + phone;
		stringRedisTemplate.delete(contractorKey);
	}

	@Override
	public Contractor find(String phone) {
		// TODO Auto-generated method stub
		if (phone == null || phone.isEmpty()) {
			return null;
		}
		if (!membersPhonebook.isMember(phone)) {
			return null;
		}
		String contractorKey = "cm:contractor:phone:" + phone;
		String name = hashOperations.get(contractorKey, "name");
		String wechat = hashOperations.get(contractorKey, "wechat");
		String applyTime = hashOperations.get(contractorKey, "applyTime");
		String handleTime = hashOperations.get(contractorKey, "handleTime");
		String contractLink = hashOperations.get(contractorKey, "contractLink");
		String shouquanJibie = hashOperations.get(contractorKey, "shouquanJibie");
		String shouquanBianma = hashOperations.get(contractorKey, "shouquanBianma");
		String shouquanRiqi = hashOperations.get(contractorKey, "shouquanRiqi");
		String message = hashOperations.get(contractorKey, "message");
		Contractor contractor = new Contractor(name, phone, wechat, applyTime, handleTime, contractLink, shouquanJibie,
				shouquanBianma, shouquanRiqi, message);
		return contractor;
	}

	@Override
	public long countApprovedContractors() {

		return membersPhonebook.countApproved();
	}

	@Override
	public long countApplicants() {
		return membersPhonebook.countApplicants();
	}

}
