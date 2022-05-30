package pt.unl.fct.di.adc.silvanus.api.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.UserData;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestUsers.CHARSET;

//TODO Review all Rest operations 
@Produces(MediaType.APPLICATION_JSON + CHARSET)
public interface RestUsers {
	static String CHARSET = ";charset=utf-8";
	String PATH = "/user";

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response register(UserData data);
	
	@POST
	@Path("/login/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response login(@PathParam("identifier") String identifier, @QueryParam("password") String password);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response logout(@CookieParam("token") String cookie);
	
	@PUT
	@Path("/promote/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response promote(@CookieParam("token") String cookie, @PathParam("identifier") String username, @QueryParam("new_role") String new_role);
	
	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getUser(@CookieParam("token") String cookie, @QueryParam("identifier") @DefaultValue(" ") String identifier);

	@GET
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response refresh_token(@CookieParam("token") String cookie);
	
	@DELETE
	@Path("/remove/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@CookieParam("token") String cookie, @PathParam("identifier") String username);
	
	@PUT
	@Path("/activate/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response activate(@CookieParam("token") String cookie, @PathParam("identifier") String identifier);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changePassword(@CookieParam("token") String cookie, @QueryParam("password") String new_password);
	
	@PUT
	@Path("/change/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changeAttributes(@CookieParam("token") String cookie, @PathParam("identifier") String identifier, @QueryParam("attributes") String list_json);
}
