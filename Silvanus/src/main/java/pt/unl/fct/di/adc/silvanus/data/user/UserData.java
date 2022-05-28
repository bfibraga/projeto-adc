package pt.unl.fct.di.adc.silvanus.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {

	public static final String SEPARATOR = ".";

	private String username;
	private String email;
	private String name;
	private String password;
	private String confirm;
	private String role;
	private String state;
	private String visibility;
	private String nif;
	private String address;
	private String telephone;
	private String smartphone;

	public UserData() {

	}

	public UserData(
			String username,
			String email, 
			String name, 
			String password,
			String confirm,
			String role,
			String state,
			String visibility,
			String nif,
			String address,
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
		this.nif = nif;
		this.address = address;
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
	
	public String getNif() {
		return this.nif;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	
	public String getSmartphone() {
		return this.smartphone;
	}

	public String getID(){
		return this.getUsername().hashCode() + SEPARATOR +
				this.getEmail().hashCode();
	}

	private boolean validField(String keyword) {
		return !keyword.trim().equals("");
	}
	
	public boolean validation() {
		boolean valid = 
				validField(this.username) 
				&& validField(this.email)
				&& validField(this.name)
				&& validField(this.password)
				&& this.password.equals(confirm);
		
		return valid;
	}

	public LoginData getCredentials() {
		return new LoginData(this.getUsername(), this.getEmail(), this.getPassword());
	}

	public UserInfoData getInfo() {
		return new UserInfoData(this.getName(), this.getState(), this.getVisibility(), this.getNif(), this.getAddress(), this.getTelephone(), this.getSmartphone());
	}

	public UserStateData getStateData() {
		//TODO Improve this later
		return new UserStateData(this.getState());
	}
}
