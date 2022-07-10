package pt.unl.fct.di.adc.silvanus.data.user;

public class LoginData {

	public final static String NOT_DEFINED = "~~";
	public final static String EMAIL_REGEX = "^(.+)@(.+)$";
	private String username;
	private String email;
	private String password;

	public LoginData() {
		this("","","");
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
		return keyword != null && !keyword.trim().equals("");
	}
	
	/**
	 * Checks if the password has length greater than 5 and contains numbers and letters.
	 * @param password to validate
	 * @return
	 */
	private boolean validPassword(String password) {
		if (!validField(password))
			return false;

		String n = ".*[0-9].*";
		String A = ".*[A-Z].*";
		String a = ".*[a-z].*";

		boolean hasLowerCaseLetters = password.matches(a);
		boolean hasUpperCaseLetters = password.matches(A);
		boolean hasNumbers = password.matches(n);

		return hasNumbers && (hasLowerCaseLetters || hasUpperCaseLetters) && password.length() >= 5;
	}

	private boolean validEmail(String email){
		if (!validField(email)){
			return false;
		}

		String e = "^(.+)@(.+)$";

		return email.matches(e);
	}

	/**
	 * Checks if the username, email and password are valid (not if they are correct)
	 * @return if they are or not
	 */
	public boolean validation() {
		return validField(this.getUsername()) && validEmail(this.getEmail()) && validPassword(this.getPassword());
	}

	public String getID(){
		return this.getUsername().hashCode() + "." +
				this.getEmail().hashCode();
	}

	@Override
	public String toString() {
		return String.format("LoginData:\n\tUsername: %s\n\tEmail: %s\n\tPassword: %s", this.getUsername(), this.getEmail(), this.getPassword());
	}
}
