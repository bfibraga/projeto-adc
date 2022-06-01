package pt.unl.fct.di.adc.silvanus.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.cloud.storage.Acl;

public class UserData {

	public static final String SEPARATOR = ".";

	private LoginData credentials;
	private String confirm_password;
	private UserInfoData info;
	private String role;
	private UserStateData state;

	public UserData() {}

	public UserData(
			LoginData credentials,
			String confirm_password,
			String role,
			UserInfoData info) {
		this(credentials, confirm_password, info, UserRole.compareType(role).getRoleName(), new UserStateData());
	}

	public UserData(
			LoginData credentials,
			String confirm_password,
			UserInfoData info,
			String role,
			UserStateData state) {
		this.credentials = credentials;
		this.confirm_password = confirm_password;
		this.info = info;
		this.role = role;
		this.state = state;
	}

	public LoginData getCredentials() {
		return this.credentials;
	}

	public String getConfirm_password() {
		return this.confirm_password;
	}

	public UserInfoData getInfo() {
		return this.info;
	}

	public String getRole() {
		if (this.role == null){
			this.role = UserRole.USER.getRoleName();
		}
		return this.role;
	}

	public UserStateData getUserStateData() {
		if (this.state == null){
			this.state = new UserStateData();
		}
		return this.state;
	}

	public String getID(){
		LoginData data = this.getCredentials();
		return data.getUsername().hashCode() + SEPARATOR +
				data.getEmail().hashCode();
	}

	private boolean validField(String keyword) {
		return !keyword.trim().equals("");
	}
	
	public boolean validation() {
		LoginData data = this.getCredentials();
		boolean valid = 
				validField(data.getUsername())
				&& validField(data.getEmail())
				&& validField(data.getPassword())
				&& data.getPassword().equals(confirm_password);
		
		return valid;
	}
}
