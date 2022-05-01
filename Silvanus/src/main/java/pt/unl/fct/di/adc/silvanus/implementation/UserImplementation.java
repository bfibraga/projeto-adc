package pt.unl.fct.di.adc.silvanus.implementation;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.UserData;
import pt.unl.fct.di.adc.silvanus.util.Result;
import pt.unl.fct.di.adc.silvanus.util.Users;

public class UserImplementation implements Users{

	@Override
	public Result<AuthToken> register(UserData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<AuthToken> login(LoginData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> logout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> promote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UserData> getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<AuthToken> getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> activate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deactivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> changePassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> changeAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
