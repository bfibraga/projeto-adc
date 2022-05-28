package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path(RestUsers.PATH)
public class UsersResource implements RestUsers {

	private final UserImplementation impl;
	public UsersResource() {
		impl = new UserImplementation();
	}

	@Override
	public Response register(UserData data) {
		Result<String> result = impl.register(data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response login(String identifier, String password) {

		LoginData data = identifier.matches(LoginData.EMAIL_REGEX) ?
				new LoginData(LoginData.NOT_DEFINED, identifier, password) :
				new LoginData(identifier, LoginData.NOT_DEFINED, password);
		Result<String> result = impl.login(data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		System.out.println("Token\n" + "token" + " -> " + result.value());
		//token = (AuthToken) result.value();
		//Add http-only cookie
		//TODO Change entity
		return Response.ok().cookie(TOKEN.cookie(result.value())).build();
	}

	@Override
	public Response logout(String token) {
		System.out.println(token);
		Result<Void> result = impl.logout(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).cookie(TOKEN.cookie(null)).build();
	}

	@Override
	public Response promote(String token, String username, String new_role) {
		Result<Void> result = impl.promote(token, username, new_role);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response getUser(String token) {
		//TODO Alter getUser implementation
		Result<String[]> result = impl.getUser(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response refresh_token(String token) {
		Result<String> result = impl.refresh_token(token);
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response remove(String token, String username) {
		Result<Void> result = impl.remove(token, username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + username + "was sucessfully removed").build();
	}

	@Override
	public Response activate(String token, String identifier) {
		Result<Void> result = impl.activate(token, identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User was sucessfully activated").build();
	}

	@Override
	public Response changePassword(String token, String new_password) {
		Result<Void> result = impl.changePassword(token, new_password);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("Password was sucessfully removed").build();
	}

	@Override
	public Response changeAttributes(String token, String identifier, String list_json) {
		Result<Void> result = impl.changeAttributes(token, identifier, list_json);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}
}
