package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.util.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.RegisterData;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;

@Path("/user")
public class UsersResource implements RestUsers {

	private final UserImplementation impl = new UserImplementation();

	public UsersResource() {
		
	}
	
	@Override
	public Response register(RegisterData data) {
		Result result = impl.register(data);
		
		if (!result.isOK()) {
			return Response.status(result.error()).build();
		}
		
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response login(String userID, String password) {
		Result result = impl.login(new LoginData(userID, "b", password));
		
		if (!result.isOK()) {
			return Response.status(result.error()).build();
		}
		
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response logout() {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response promote(String username) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response getUser(String username) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response getToken(String username) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response remove(String username) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response activate(String username, boolean value) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response changePassword(AuthToken token, String new_password) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	@Override
	public Response changeAttributes(String username) {
		// TODO Auto-generated method stub
		return Response.ok().build();
	}

	
}
