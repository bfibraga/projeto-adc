package pt.unl.fct.di.adc.silvanus.data.user;

import pt.unl.fct.di.adc.silvanus.implementation.user.perms.UserRole;

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
			UserInfoData info) {
		this(credentials, confirm_password, info, "USER", new UserStateData());
	}

	public UserData(
			LoginData credentials,
			String confirm_password,
			String role,
			UserInfoData info) {
		this(credentials, confirm_password, info, role, new UserStateData());
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
			this.role = UserRole.ENDUSER.getRoleName();
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
		return data.getID();
	}

	private boolean validField(String keyword) {
		return keyword !=null && !keyword.trim().equals("");
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

	@Override
	public String toString() {
		return String.format("%s\nConfirm: %s\n%s\nRole: %s\n%s", this.getCredentials(), this.getConfirm_password(), this.getInfo(), this.getRole(), this.getUserStateData());
	}
}
