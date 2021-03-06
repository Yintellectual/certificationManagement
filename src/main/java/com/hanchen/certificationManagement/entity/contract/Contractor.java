package com.hanchen.certificationManagement.entity.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contractor {
    private String name;
	private String phone;
	private String wechat;
	private String applyTime;
	private String handleTime;
	private String contractLink;
	private String shouquanJibie;
	private String shouquanBianma;
	private String shouquanRiqi;
	//private String shouquanma;
	private String message;
	public Contractor(String name, String phone, String wechat){
		this.name = name;
		this.phone = phone;
		this.wechat = wechat;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getPhone(){
		return phone;
	}
	public void setpPhone(String phone){
		this.phone = phone;
	}
	
	public String getWechat(){
		return wechat;
	}
	public void setWechat(String wechat){
		this.wechat = wechat;
	}
}
