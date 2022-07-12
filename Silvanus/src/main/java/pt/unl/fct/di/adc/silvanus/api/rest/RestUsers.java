package pt.unl.fct.di.adc.silvanus.api.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.result.LogoutData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.*;

//TODO Review all Rest operations
public interface RestUsers {
	String PATH = "/user";
	String PASSWORD = "password";
	String NEW_PASSWORD = "new_" + PASSWORD;
	String OLD_PASSWORD = "old_" + PASSWORD;
	String ROLE = "role";
	String ATTRIBUTES = "attributes";

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response register(UserData data);

	@POST
	@Path("/build")
	@Consumes(MediaType.APPLICATION_JSON)
	Response build(@HeaderParam("secret") String secret, UserData userData);
	
	@POST
	@Path("/login/{"+ IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response login(@PathParam(IDENTIFIER) String identifier, @QueryParam(PASSWORD) String password);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response logout(@CookieParam(TOKEN) String cookie, LogoutData data);
	
	@PUT
	@Path("/promote/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response promote(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String username, @QueryParam(ROLE) String new_role, @QueryParam("PlaceOfInfluence") String placeOfInfluence);
	
	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getUser(@CookieParam(TOKEN) String cookie, @QueryParam(IDENTIFIER) @DefaultValue(DEFAULT_VALUE) String identifier);

	@GET
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response refresh_token(@CookieParam(TOKEN) String cookie);
	
	@DELETE
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@CookieParam(TOKEN) String cookie, @QueryParam(IDENTIFIER) @DefaultValue(" ") String username);
	
	@PUT
	@Path("/activate/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response activate(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String identifier, @QueryParam("code") @DefaultValue(" ") String code, @QueryParam("value") @DefaultValue("true") boolean value);

	@GET
	@Path("/code/{"+ IDENTIFIER + "}")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response newCode(@PathParam(IDENTIFIER) String identifier);

	@PUT
	@Path("/change/password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changePassword(@CookieParam(TOKEN) String cookie, @QueryParam(PASSWORD) @DefaultValue(DEFAULT_VALUE) String new_password);
	
	@PUT
	@Path("/change/attributes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changeAttributes(@CookieParam(TOKEN) String cookie, @QueryParam(IDENTIFIER) @DefaultValue(DEFAULT_VALUE) String identifier, UserInfoData infoData);
}
