package com.nikola.user.login;

public class UserData {

	public String userid;
	public String email;
	public String password;
	public String device_type;
	public String login_by;
	public String device_token;
	public String timezone;
	
	public String first_name;
	public String last_name;
	public String mobile;
	public String currency;
	public String referral_code;
	public String country_code;
	public String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public String getLogin_by() {
		return login_by;
	}
	public void setLogin_by(String login_by) {
		this.login_by = login_by;
	}
	public String getDevice_token() {
		return device_token;
	}
	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getReferral_code() {
		return referral_code;
	}
	public void setReferral_code(String referral_code) {
		this.referral_code = referral_code;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}



	@Override
	public String toString() {
		return "email=" + email
				+ "&password=" + password
				+ "&device_type=" + device_type
				+ "&login_by=" + login_by
				+ "&device_token=" + device_token
				+ "&timezone=" + timezone
				+ "&first_name=" +first_name
				+ "&last_name="+last_name
				+ "&mobile="+mobile
				+ "&currency="+currency
				+ "&referral_code="+referral_code
				+ "&username="+username
				+ "&country_code="+country_code;
	}
	
	
}
