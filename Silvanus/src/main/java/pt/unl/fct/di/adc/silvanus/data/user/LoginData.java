package pt.unl.fct.di.adc.silvanus.data.user;

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

	/**
	 * 
	 * @return the user's username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * 
	 * @return the user's email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * 
	 * @return the user's password
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Checks if the given keyword is not equal to an empty string or null
	 * @param keyword to validate
	 * @return if the keyword is valid or not
	 */
	private boolean validField(String keyword) {
		return !keyword.trim().equals("");
	}
	
	/**
	 * Checks if the password has length greater than 5 and contains numbers and letters.
	 * @param password to validate
	 * @return
	 */
	private boolean validPassword(String password) {
		return password.length() > 5 && password.matches("/^[0-9A-Za-z]+$/");
	}

	/**
	 * Checks if the username, email and password are valid (not if they are correct)
	 * @return if they are or not
	 */
	public boolean validation() {
		return validField(username) && validField(email) && validField(password) && validPassword(password);
	}

}
