package pt.unl.fct.di.adc.silvanus.resources;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

@Path("/user")
public class UsersResource implements RestUsers {

	private final UserImplementation impl = new UserImplementation();
	private AuthToken token;

	public UsersResource() {

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
	public Response login(LoginData data) {

		Result result = impl.login(data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		//token = (AuthToken) result.value();

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response logout() {
		Result result = impl.logout(token);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User sucessfully logged out").build();
	}

	@Override
	public Response promote(String username, String new_role) {
		Result result = impl.promote(token, username, new_role);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + username + "was promoted to " + new_role).build();
	}

	@Override
	public Response getUser(String username) {
		Result result = impl.getUser(username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response getToken(String username) {
		Result result = impl.getToken(username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(token).build();
	}

	@Override
	public Response remove(String username) {
		Result result = impl.remove(token, username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + username + "was sucessfully removed").build();
	}

	@Override
	public Response activate(String username) {
		Result result = impl.activate(token, username);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User was sucessfully activated").build();
	}

	@Override
	public Response changePassword(AuthToken token, String new_password) {
		Result result = impl.changePassword(token, new_password);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("Password was sucessfully removed").build();
	}

	@Override
	public Response changeAttributes(String username, String list_json) {
		Result result = impl.changeAttributes(token, username, list_json);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User atttibutes were sucessfully changed").build();
	}

}
