package pt.unl.fct.di.adc.silvanus.util.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.UserData;

import static pt.unl.fct.di.adc.silvanus.util.interfaces.RestUsers.CHARSET;

//TODO Review all Rest operations 
@Produces(MediaType.APPLICATION_JSON + CHARSET)
public interface RestUsers {
	String CHARSET = ";charset=utf-8";

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response register(UserData data);
	
	@POST
	@Path("/login/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response login(@PathParam("identifier") String identifier, @QueryParam("password") String password,
				   HttpServletResponse response);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response logout(@Context HttpServletRequest request);
	
	@PUT
	@Path("/promote/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response promote(String token, @PathParam("username") String username, @QueryParam("new_role") String new_role);
	
	@GET
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getUser(@PathParam("username") String username);

	@GET
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response refresh_token(@CookieParam("rt") String old_refresh_token);
	
	@GET
	@Path("/token/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getToken(@PathParam("identifier") String identifier);
	
	@DELETE
	@Path("/remove/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@PathParam("identifier") String username);
	
	@PUT
	@Path("/activate/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response activate(@PathParam("identifier") String identifier, String token);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changePassword(String token, @QueryParam("password") String new_password);
	
	@PUT
	@Path("/change/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changeAttributes(@PathParam("identifier") String identifier, @QueryParam("attributes") String list_json, String token);
}
