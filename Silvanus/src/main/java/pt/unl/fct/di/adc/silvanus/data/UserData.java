package pt.unl.fct.di.adc.silvanus.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {

	private String username;
	private String email;
	private String name;
	private String password;
	// TODO verificar se e preciso | private String confirm;
	private String role;
	// TODO verificar se e preciso | private String state;
	// TODO acho que este nao e necessario | private String visibility;
	private String telephone;
	// TODO este nao e preciso se temos o outro private String smartphone;

	public UserData() {

	}

	public UserData(String username, String email, String name, String password, String role, String telephone) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.password = password;
		// this.confirm = confirm;
		this.role = role;
		// this.state = state;
		// this.visibility = visibility;
		this.telephone = telephone;
		// this.smartphone = smartphone;
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
	 * @return the user's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return the user's password
	 */
	public String getPassword() {
		return this.password;
	}

	/*
	 * public String getConfirmation() { return this.confirm; }
	 */

	/**
	 * 
	 * @return the user's role
	 */
	public String getRole() {
		return this.role;
	}

	/*
	 * public String getState() { return this.state; }
	 */

	/*
	 * public String getVisibility() { return this.visibility; }
	 */

	/**
	 * 
	 * @return the user's telephone number
	 */
	public String getTelephone() {
		return this.telephone;
	}
	/*
	 * public String getSmartphone() { return this.smartphone; }
	 */

	/**
	 * Checks if the given keyword is not equal to an empty string or null
	 * 
	 * @param keyword to validate
	 * @return if the keyword is valid or not
	 */
	private boolean validField(String keyword) {
		return !keyword.trim().equals("") || !(keyword == null);
	}

	/**
	 * Checks if the password has length greater than 5 and contains numbers and
	 * letters.
	 * 
	 * @param password to validate
	 * @return
	 */
	private boolean validPassword(String password) {
		return password.length() > 5 && password.matches("/^[0-9A-Za-z]+$/");
	}

	/**
	 * Checks if the user's info is valid.
	 * 
	 * @return if it is or not
	 */
	public boolean validation() {
		if (!validField(this.username))
			return false;

		if (!validField(this.email) || !(this.email.matches("(.*)@(.*)")))
			return false;

		if (!validField(this.name))
			return false;

		if (!validField(this.password) || !validPassword(password))
			return false;

		if (!validField(role))
			return false;

		return true;
	}
}
