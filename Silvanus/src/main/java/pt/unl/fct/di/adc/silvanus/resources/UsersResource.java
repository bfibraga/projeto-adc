package pt.unl.fct.di.adc.silvanus.resources;

import java.util.logging.Logger;

import com.google.gson.*;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.UserData;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.Result;

public class UsersResource implements RestUsers {

	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());
	private final UserImplementation impl = new UserImplementation();
	private final Gson g = new Gson();

	public UsersResource() {}
	
	@Override
	public String register(UserData data) {
		Result<AuthToken> result = impl.register(data);
		
		return g.toJson(result.value());
	}

	@Override
	public String login(LoginData data) {
		
		Result<AuthToken> result = impl.login(data);
		
		return g.toJson(result.value());
	}


	@Override
	public String logout() {
		Result<Void> result = impl.logout();
		
		return g.toJson(result.value());
	}

	@Override
	public String promote(String username) {
		// TODO Auto-generated method stub
		Result<Void> result = impl.promote();
		
		return g.toJson(result.value());
	}

	@Override
	public String getUser(String username) {
		Result<UserData> result = impl.getUser();
		
		return g.toJson(result.value());
	}

	@Override
	public String getToken(String username) {
		Result<AuthToken> result = impl.getToken();
		
		return g.toJson(result.value());
	}

	@Override
	public String remove(String username) {
		Result<Void> result = impl.remove();
		
		return g.toJson(result.value());
	}

	/*
	 * @Override public void activate(String username, boolean value) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public String changePassword(AuthToken token, String new_password) {
		Result<Void> result = impl.changePassword();
		
		return g.toJson(result.value());
	}

	@Override
	public String changeAttributes(String username) {
		Result<Void> result = impl.changeAttributes();
		
		return g.toJson(result.value());
	}

}
