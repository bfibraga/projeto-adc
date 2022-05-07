package pt.unl.fct.di.adc.silvanus.util;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;

import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.RegisterData;

public interface Users {

	Result<AuthToken> register(RegisterData data);
	
	Result<AuthToken> login(LoginData data);
	
	Result<Void> logout();
	
	Result<Void> promote();
	
	Result<RegisterData> getUser();
	
	Result<AuthToken> getToken();
	
	Result<Void> remove();
	
	Result<Void> activate();
	
	Result<Void> deactivate();
	
	Result<Void> changePassword();
	
	Result<Void> changeAttributes();
	
}
