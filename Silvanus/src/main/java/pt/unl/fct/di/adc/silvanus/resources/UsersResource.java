package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.UserData;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;

public class UsersResource implements RestUsers {

	private final UserImplementation impl = new UserImplementation();

	@Override
	public AuthToken register(UserData data) {
		return impl.register(data).value();
	}

	@Override
	public AuthToken login(String userID, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		impl.logout();
	}

	@Override
	public void promote(String username) {
		// TODO Auto-generated method stub
		impl.promote();
	}

	@Override
	public UserData getUser(String username) {
		// TODO Auto-generated method stub
		return impl.getUser().value();
	}

	@Override
	public AuthToken getToken(String username) {
		// TODO Auto-generated method stub
		return impl.getToken().value();
	}

	@Override
	public void remove(String username) {
		// TODO Auto-generated method stub
		impl.remove();
	}

	/*
	 * @Override public void activate(String username, boolean value) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public void changePassword(AuthToken token, String new_password) {
		// TODO Auto-generated method stub
		impl.changePassword();
	}

	@Override
	public void changeAttributes(String username) {
		// TODO Auto-generated method stub
		impl.changeAttributes();
	}

}
