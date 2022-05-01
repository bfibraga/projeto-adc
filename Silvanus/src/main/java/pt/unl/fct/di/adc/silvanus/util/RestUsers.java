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

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.UserData;

//TODO Review all Rest operations 
@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public interface RestUsers {
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken register(UserData data);
	
	@POST
	@Path("/login/{userID}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken login(@PathParam("username") String userID, @QueryParam("password") String password);
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void logout();
	
	@PUT
	@Path("/promote/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void promote(@PathParam("username") String username);
	
	@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	UserData getUser(@QueryParam("username") String username);
	
	@GET
	@Path("/token/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken getToken(@PathParam("username") String username);
	
	@DELETE
	@Path("/remove/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	void remove(@PathParam("username") String username);
	
	@PUT
	@Path("/activate/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void activate(@PathParam("username") String username, @QueryParam("value") boolean value);
	
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void changePassword(AuthToken token, @QueryParam("password") String new_password);
	
	@PUT
	@Path("/change/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void changeAttributes(@PathParam("username") String username);
}
