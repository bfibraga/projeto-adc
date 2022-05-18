package pt.unl.fct.di.adc.silvanus.util;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Context;

public interface Users {

	Result<String> register(UserData data);
	
	Result<String> login(LoginData data);
	
	Result<Void> logout(String token);
	
	Result<Void> promote(String token, String username, String new_role);
	
	Result<String[]> getUser(String username);

	Result<String> refresh_token(String old_refresh_token);
	
	Result<AuthToken> getToken(String username);
	
	Result<Void> remove(String token, String username);
	
	Result<Void> activate(String token, String username);
	
	Result<Void> changePassword(String token, String new_password);
	
	Result<Void> changeAttributes(String token, String target_username, String list_json);
	
}
