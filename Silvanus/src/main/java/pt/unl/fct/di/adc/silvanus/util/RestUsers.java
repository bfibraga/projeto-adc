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
	/**
	 * 
	 * @param data -> needed to register a user includes all the atributtes
	 *             described in the class "UserData"
	 * @return a token that will be used to login the user
	 */
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken register(UserData data);

	/**
	 * This method takes in a userID and a password and generates a token to login
	 * the user
	 * 
	 * @param userID   -> the id of the user (in the class "UserData" it is called
	 *                 "username")
	 * @param password -> the password of the user
	 * @return a token that will be used to login the user
	 */
	@POST
	@Path("/login/{userID}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken login(@PathParam("username") String userID, @QueryParam("password") String password);

	/**
	 * This method will be used to logout the user
	 */
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void logout();

	/**
	 * This method is used to promote a user to a role higher than his current one.
	 * 
	 * @param username -> the username of the user to be promoted
	 */
	@PUT
	@Path("/promote/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void promote(@PathParam("username") String username);

	/**
	 * This method is used to get the data of a particular user.
	 * 
	 * @param username -> the username of the user who'se information we want to
	 *                 obtain
	 * @return the user's information
	 */
	@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	UserData getUser(@QueryParam("username") String username);

	/**
	 * This method is used to obtain the authentitication token of a particular
	 * user.
	 * 
	 * @param username -> the username of the user who'se token we want to obtain
	 * @return the token of the user
	 */
	@GET
	@Path("/token/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	AuthToken getToken(@PathParam("username") String username);

	/**
	 * This method is used to delete a user from the system's database.
	 * 
	 * @param username -> the username of the user we want to remove from the DB
	 */
	@DELETE
	@Path("/remove/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	void remove(@PathParam("username") String username);

	/**
	 * This method is used to validate the user. To give him access to the app.
	 * 
	 * @param username -> of the user who we are going to activate
	 * @param value    -> the user's new value after being validated
	 * 
	 * @PUT @Path("/activate/{username}")
	 * @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON +
	 *                                       ";charset=utf-8") void
	 *                                       activate(@PathParam("username") String
	 *                                       username, @QueryParam("value") boolean
	 *                                       value);
	 */

	/**
	 * This method is used to change the password of a particular user.
	 * 
	 * @param token        -> token of the user who'se password is going to change
	 * @param new_password -> new password of the user
	 */
	@PUT
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void changePassword(AuthToken token, @QueryParam("password") String new_password);

	/**
	 * This method is used to change all or some of the attributes of the user that are not
	 * covered by another method in this class.
	 * 
	 * @param username -> username of the user who'se attributes are going to be
	 *                 changed
	 */
	@PUT
	@Path("/change/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	void changeAttributes(@PathParam("username") String username);
}
