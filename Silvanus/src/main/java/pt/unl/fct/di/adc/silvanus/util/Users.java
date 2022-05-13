package pt.unl.fct.di.adc.silvanus.util;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Users {

	Result<AuthToken> register(UserData data);
	
	Result<AuthToken> login(LoginData data);
	
	Result<Void> logout(AuthToken token);
	
	Result<Void> promote(AuthToken token, String username, String new_role);
	
	Result<String[]> getUser(String username);
	
	Result<AuthToken> getToken(String username);
	
	Result<Void> remove(AuthToken token, String username);
	
	Result<Void> activate(AuthToken token, String username);
	
	Result<Void> changePassword(AuthToken token, String new_password);
	
	Result<Void> changeAttributes(AuthToken token, String target_username, String list_json);
	
}
