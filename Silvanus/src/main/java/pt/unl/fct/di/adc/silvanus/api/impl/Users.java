package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.user.*;
import pt.unl.fct.di.adc.silvanus.data.user.result.LogoutData;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.List;

public interface Users {

	Result<String> register(UserData data);
	Result<String> build(UserData data);
	
	Result<String> login(LoginData data);
	
	Result<Void> logout(String token, LogoutData data);
	
	Result<Void> promote(String token, String identifier, String new_role);

	Result<List<UserInfoVisible>> getUser(String token, String identifier);

	Result<String> refresh_token(String old_refresh_token);

	Result<Void> remove(String userID, String identifier);
	
	Result<Void> activate(String token, String username);
	
	Result<Void> changePassword(String token, String new_password);
	
	Result<UserInfoData> changeAttributes(String token, String target_username, UserInfoData infoData);
}
