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
	String CHARSET = ";charset=utf-8";
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
	Response logout(@Context HttpServletRequest request);
	
	@PUT
	@Path("/promote/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response promote(@Context HttpServletRequest request, @PathParam("identifier") String username, @QueryParam("new_role") String new_role);
	
	@GET
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response getUser(@Context HttpServletRequest request);

	@GET
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response refresh_token(@Context HttpServletRequest request);
	
	@DELETE
	@Path("/remove/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@Context HttpServletRequest request, @PathParam("identifier") String username);
	
	@PUT
	@Path("/activate/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response activate(@Context HttpServletRequest request, @PathParam("identifier") String identifier);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changePassword(@Context HttpServletRequest request, @QueryParam("password") String new_password);
	
	@PUT
	@Path("/change/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + CHARSET)
	Response changeAttributes(@Context HttpServletRequest request, @PathParam("identifier") String identifier, @QueryParam("attributes") String list_json);
}
