package pt.unl.fct.di.adc.silvanus.data;

public class LoginData {

	private String username;
	private String email;
	private String password;

	public LoginData() {
	}

	public LoginData(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}
	
	private boolean validField(String keyword) {
		return !keyword.trim().equals("");
	}

	public boolean validation() {
		if(!validField(username) || !validField(email) || !validField(password)) {
			return false;
		}
		return true;
	}

}
