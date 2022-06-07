package pt.unl.fct.di.adc.silvanus.api.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.UserData;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.*;

//TODO Review all Rest operations
@Path(RestUsers.PATH)
@Produces(MediaType.APPLICATION_JSON + CHARSET)
public interface RestUsers {
	String PATH = "/user";
	String PASSWORD = "password";
	String ROLE = "role";
	String ATTRIBUTES = "attributes";

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response register(UserData data);
	
	@POST
	@Path("/login/{"+ IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response login(@PathParam(IDENTIFIER) String identifier, @QueryParam(PASSWORD) String password);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response logout(@CookieParam(TOKEN) String cookie);
	
	@PUT
	@Path("/promote/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response promote(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String username, @QueryParam(ROLE) String new_role);
	
	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getUser(@CookieParam(TOKEN) String cookie, @QueryParam(IDENTIFIER) @DefaultValue(DEFAULT_VALUE) String identifier);

	@GET
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response refresh_token(@CookieParam(TOKEN) String cookie);
	
	@DELETE
	@Path("/remove/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String username);
	
	@PUT
	@Path("/activate/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response activate(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String identifier);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changePassword(@CookieParam(TOKEN) String cookie, @QueryParam(PASSWORD) String new_password);
	
	@PUT
	@Path("/change/{" + IDENTIFIER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changeAttributes(@CookieParam(TOKEN) String cookie, @PathParam(IDENTIFIER) String identifier, @QueryParam(ATTRIBUTES) String list_json);
}
