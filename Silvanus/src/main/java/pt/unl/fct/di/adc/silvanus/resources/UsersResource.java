package pt.unl.fct.di.adc.silvanus.resources;

import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserStateData;
import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInData;
import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInVisibleData;
import pt.unl.fct.di.adc.silvanus.data.user.result.LogoutData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.implementation.user.UserImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestUsers;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path(RestUsers.PATH)
public class UsersResource implements RestUsers {

	private static final UserImplementation impl = new UserImplementation();
	public UsersResource() {

	}

	@Override
	public Response register(UserData data) {
		Result<String> result = impl.register(data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		//TODO Testing
		//Build not essential entities
		/*Queue queue = QueueFactory.getDefaultQueue();
		String url = String.format("%s%s%s", RestInterface.PATH, RestUsers.PATH, "/build");
		System.out.println(url);
		queue.add(TaskOptions.Builder.withUrl(url)
						.param("userData", JSON.encode(data))
				.param("secret", TOKEN.createNewJWS("silvanus:build", 1000, new HashSet<>())));*/

		return Response.ok().cookie(TOKEN.cookie(result.value())).build();
	}

	@Override
	public Response build(String secret, UserData userData) {
		System.out.println(secret);
		System.out.println(userData);
		//Token verifycation
		Claims jws = TOKEN.verifyToken(secret);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<String> result = impl.build(userData);

		if(!result.isOK()){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong with building user " + userData.getCredentials().getUsername()).build();
		}

		return Response.ok(result.value()).build();
	}

	@Override
	public Response login(String identifier, String password) {

		LoginData data = identifier.matches(LoginData.EMAIL_REGEX) ?
				new LoginData(LoginData.NOT_DEFINED, identifier, password) :
				new LoginData(identifier, LoginData.NOT_DEFINED, password);
		Result<LoggedInData> result = impl.login(data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).cookie(TOKEN.cookie(null)).build();
		}

		//Add http-only cookie
		return Response.ok().cookie(TOKEN.cookie(result.value().getToken())).entity(result.value().getStateData()).build();
	}

	@Override
	public Response logout(String token, LogoutData data) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<Void> result = impl.logout(jws.getSubject(), data);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).cookie(TOKEN.cookie(null)).build();
	}

	@Override
	public Response promote(String token, String username, String new_role, String placeOfInfluence) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		/*Set<String> scope = jws.get("scope", HashSet.class);

		System.out.println(scope);*/

		Result<Void> result = impl.promote(jws.getSubject(), username, new_role, placeOfInfluence);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.statusMessage()).build();
	}

	@Override
	public Response getUser(String token, String identifier) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		//TODO Alter getUser implementation
		Result<List<UserInfoVisible>> result = impl.getUser(jws.getSubject(), identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response refresh_token(String token) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<String> result = impl.refresh_token(token);
		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response remove(String token, String identifier) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<Void> result =  impl.remove(jws.getSubject(), identifier);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User " + identifier + " was sucessfully removed").build();
	}

	@Override
	public Response activate(String token, String identifier, String code, boolean value) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<Void> result = impl.activate(jws.getSubject(), identifier, code, value);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("User was sucessfully activated").build();
	}

	@Override
	public Response newCode(String identifier) {
		Result<String> result = impl.newActivationCode(identifier);

		if (!result.isOK()){
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}

	@Override
	public Response changePassword(String token, String new_password) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		Result<Void> result = impl.changePassword(jws.getSubject(), new_password);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity("Password was sucessfully removed").build();
	}

	@Override
	public Response changeAttributes(String token, String identifier, UserInfoData infoData) {
		//Token verifycation
		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
		}

		System.out.println(infoData.toString());

		Result<UserInfoData> result = identifier.trim().equals("") ?
				impl.changeAttributes(jws.getSubject(), jws.getSubject(), infoData) :
				impl.changeAttributes(jws.getSubject(), identifier, infoData);

		if (!result.isOK()) {
			return Response.status(result.error()).entity(result.statusMessage()).build();
		}

		return Response.ok().entity(result.value()).build();
	}
}
