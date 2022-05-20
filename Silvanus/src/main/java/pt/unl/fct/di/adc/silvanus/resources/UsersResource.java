package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/user")
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

		System.out.println(result.value());
		//token = (AuthToken) result.value();
		//Add http-only cookie
		//TODO Change entity
		return Response.ok().header("Set-Cookie", "tkn=" + result.value() + "; HttpOnly ; Max-Age=1000*60*60").build();
	}

	@Override
	public Response logout(HttpServletRequest request) {
		String token = this.token(request);

		System.out.println(token);
		Result<Void> result = impl.logout(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}


		return Response.ok().entity(result.statusMessage()).header("Set-Cookie", null).build();
	}

	@Override
	public Response promote(HttpServletRequest request, String username, String new_role) {
		String token = this.token(request);

		Result<Void> result = impl.promote(token, username, new_role);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response getUser(HttpServletRequest request) {
		String token = this.token(request);

		//TODO Alter getUser implementation
		Result<String[]> result = impl.getUser(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response refresh_token(HttpServletRequest request) {
		String token = this.token(request);
		Result<String> result = impl.refresh_token(token);
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response getToken(HttpServletRequest request) {
		String token = this.token(request);

		Result<AuthToken> result = impl.getToken(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response remove(HttpServletRequest request, String username) {
		String token = this.token(request);

		Result<Void> result = impl.remove(token, username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + username + "was sucessfully removed").build();
	}

	@Override
	public Response activate(HttpServletRequest request, String identifier) {
		String token = this.token(request);

		Result<Void> result = impl.activate(token, identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User was sucessfully activated").build();
	}

	@Override
	public Response changePassword(HttpServletRequest request, String new_password) {
		String token = this.token(request);

		Result<Void> result = impl.changePassword(token, new_password);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("Password was sucessfully removed").build();
	}

	@Override
	public Response changeAttributes(HttpServletRequest request, String identifier, String list_json) {
		String token = this.token(request);

		Result<Void> result = impl.changeAttributes(token, identifier, list_json);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User atttibutes were sucessfully changed").build();
	}

	private String token(HttpServletRequest request){
		//TODO Debug
		for (Cookie cookie: request.getCookies()) {
			System.out.println(cookie.getName() + " -> " + cookie.getValue());
			if (cookie.getName().equals("tkn")){
				return cookie.getValue();
			}
		}
		return null;
	}
}
