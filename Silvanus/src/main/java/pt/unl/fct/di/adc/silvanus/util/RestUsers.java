package pt.unl.fct.di.adc.silvanus.util;

import javax.ws.rs.Consumes;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;

//TODO Review all Rest operations 
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public interface RestUsers {
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response register(UserData data);
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response login(LoginData data);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response logout();
	
	@PUT
	@Path("/promote/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response promote(@PathParam("username") String username, @QueryParam("new_role") String new_role);
	
	@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response getUser(@QueryParam("username") String username);
	
	@GET
	@Path("/token/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response getToken(@PathParam("username") String username);
	
	@DELETE
	@Path("/remove/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response remove(@PathParam("username") String username);
	
	@PUT
	@Path("/activate/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response activate(@PathParam("username") String username);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response changePassword(AuthToken token, @QueryParam("password") String new_password);
	
	@PUT
	@Path("/change/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	Response changeAttributes(@PathParam("username") String username, @QueryParam("attributes") String list_json);
}
