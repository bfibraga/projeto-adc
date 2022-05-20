package pt.unl.fct.di.adc.silvanus.resources;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.Date;

@Path("/user")
public class UsersResource implements RestUsers {

	private final UserImplementation impl;
	public UsersResource() {
		impl = new UserImplementation();
	}

	@Override
	public Response register(UserData data) {
		Result result = impl.register(data);

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

		System.out.println(result.value());
		//token = (AuthToken) result.value();
		//Add http-only cookie
		//TODO Change entity
		return Response.ok().header("Set-Cookie", "tkn=" + result.value() + "; HttpOnly ; Max-Age=1000*60*60").build();
	}

	@Override
	public Response logout(HttpServletRequest request) {
		String token = "";

		//TODO Debug
		for (Cookie cookie: request.getCookies()) {
			System.out.println(cookie.getName() + " -> " + cookie.getValue());
			if (cookie.getName().equals("tkn")){
				token = cookie.getValue();
			}
		}

		System.out.println(token);
		Result<Void> result = impl.logout(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}


		return Response.ok().entity(result.statusMessage()).header("Set-Cookie", null).build();
	}

	@Override
	public Response promote(String token, String username, String new_role) {
		Result result = impl.promote(token, username, new_role);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response getUser(String identifier) {
		Result result = impl.getUser(identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response refresh_token(String old_refresh_token) {
		Result<String> result = impl.refresh_token(old_refresh_token);
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response getToken(String identifier) {
		Result result = impl.getToken(identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response remove(String username) {
		Result result = impl.remove("", username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + username + "was sucessfully removed").build();
	}

	@Override
	public Response activate(String identifier, String token) {
		Result result = impl.activate(token, identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User was sucessfully activated").build();
	}

	@Override
	public Response changePassword(String token, String new_password) {
		Result result = impl.changePassword(token, new_password);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("Password was sucessfully removed").build();
	}

	@Override
	public Response changeAttributes(String identifier, String list_json, String token) {
		Result result = impl.changeAttributes(token, identifier, list_json);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User atttibutes were sucessfully changed").build();
	}

}
