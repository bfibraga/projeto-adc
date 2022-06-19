package pt.unl.fct.di.adc.silvanus.api.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Context;
import java.util.Set;

public interface Users {

	Result<String> register(UserData data);
	Result<String> build(UserData data);
	
	Result<String> login(LoginData data);
	
	Result<Void> logout(String token);
	
	Result<Void> promote(String token, String identifier, String new_role);

	Result<Set<UserInfoVisible>> getUser(String token, String identifier);

	Result<String> refresh_token(String old_refresh_token);

	Result<Void> remove(String token, String identifier);
	
	Result<Void> activate(String token, String username);
	
	Result<Void> changePassword(String token, String new_password);
	
	Result<UserInfoData> changeAttributes(String token, String target_username, UserInfoData infoData);
}
