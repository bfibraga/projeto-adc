package pt.unl.fct.di.adc.silvanus.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterData {

	private String username;
	private String email;
	private String name;
	private String password;
	private String confirm;
	private String role;
	private String state;
	private String visibility;
	private String telephone;
	private String smartphone;

	public RegisterData() {

	}

	public RegisterData(
			String username,
			String email, 
			String name, 
			String password,
			String confirm,
			String role,
			String state,
			String visibility,
			String telephone,
			String smartphone) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.password = password;
		this.confirm = confirm;
		this.role = role;
		this.state = state;
		this.visibility = visibility;
		this.telephone = telephone;
		this.smartphone = smartphone;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getConfirmation() {
		return this.confirm;
	}
	
	public String getRole() {
		return this.role;
	}
	
	public String getState() {
		return this.state;
	}
	
	public String getVisibility() {
		return this.visibility;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	
	public String getSmartphone() {
		return this.smartphone;
	}

	private boolean validField(String keyword) {
		return !keyword.trim().equals("");
	}
	
	public boolean validation() {
		if (!validField(this.username)) {
			return false;
		}
		if (!validField(this.email) || !(this.email.matches("(.*)@(.*)"))) {
			return false;
		}
		if (!validField(this.name)) {
			return false;
		}
		if (!validField(this.password)) {
			return false;
		}
		/*if(!(this.password.equals(confirm))) {
			return ValidationDataCode.PM;
		}*/
		
		return true;
	}
}
