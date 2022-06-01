package pt.unl.fct.di.adc.silvanus.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pt.unl.fct.di.adc.silvanus.util.cripto.CRIPTO;
import pt.unl.fct.di.adc.silvanus.util.cripto.CriptoManager;

public class LoginData {

	public final static String NOT_DEFINED = "~~";
	public final static String EMAIL_REGEX = "^(.+)@(.+)$";
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
	 *
	 * @return the user's username
	 */
	public LoginData setUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 *
	 * @return the user's username
	 */
	public LoginData setEmail(String email) {
		this.email = email;
		return this;
	}

	/**
	 *
	 * @return the user's username
	 */
	public LoginData setPassword(String password) {
		this.password = password;
		return this;
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

	public String getID(){
		return this.getUsername().hashCode() + "." +
				this.getEmail().hashCode();
	}

	@Override
	public String toString() {
		return String.format("Username: %s\nEmail: %s\nPassword: %s\n", this.getUsername(), this.getEmail(), this.getPassword());
	}
}
