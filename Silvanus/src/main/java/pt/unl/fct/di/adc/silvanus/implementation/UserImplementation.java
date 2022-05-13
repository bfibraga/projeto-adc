package pt.unl.fct.di.adc.silvanus.implementation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.Query.*;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.silvanus.data.*;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserRole;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.util.*;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public class UserImplementation implements Users {

	// Util classes
	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());
	private final Gson g = new Gson();

	// Datastore
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = this.datastore.newKeyFactory().setKind("UserCredentials");

	// User info
	private ConcurrentHashMap<String, UserData> userInfo = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AuthToken> userToken = new ConcurrentHashMap<>();

	@Override
	public Result<AuthToken> register(UserData data) {
		LOG.fine("Resgiter user " + data.getUsername());

		boolean validation_code = data.validation();
		if (validation_code) {
			LOG.warning("User " + data.getUsername() + "tryied to register with some empty important information");
			return Result.error(Response.Status.BAD_REQUEST, "User " + data.getUsername() + "tryied to register with some empty important information");
		}
		String user_id = data.getUsername();

		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);

		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {

			// Verify if user exists
			Entity user = txn.get(userKey);

			if (user != null) {
				txn.rollback();
				LOG.fine("Username " + data.getUsername() + "already exists\nTry again with another fancy nickname");
				return Result.error(Response.Status.FORBIDDEN, "Username " + data.getUsername() + "already exists\nTry again with another fancy nickname");
			}

			// Create a new User
			user = Entity.newBuilder(userKey).set("usr_email", data.getEmail())
					.set("usr_password", DigestUtils.sha512Hex(data.getPassword()))
					// .set("usr_confirmation", DigestUtils.sha512Hex(data.getConfirmation()))
					.build();

			// Role attribution
			UserRole role = UserRole.compareType(data.getRole());

			Entity userRole = Entity.newBuilder(userRoleKey).set("role_name", role.toString())
					.set("role_priority", role.toString()).build();

			// Info of the new user
			Entity userInfo = Entity.newBuilder(userInfoKey).set("usr_visibility", data.getVisibility())
					.set("usr_name", data.getName()).set("usr_telephone", data.getTelephone())
					.set("usr_smartphone", data.getSmartphone()).set("usr_address", data.getAddress())
					.set("usr_NIF", data.getNif()).build();

			// Verification of this user
			String verified = "";
			Entity userPermission;
			if (role.toString().equals("SU")) {
				verified = data.getUsername().trim();
				userPermission = Entity.newBuilder(userPermissionKey).set("usr_state", "ACTIVE")
						.set("list_usr_validation", verified).build();
			} else {
				userPermission = Entity.newBuilder(userPermissionKey).set("usr_state", data.getState())
						.set("list_usr_validation", verified).build();
			}

			AuthToken at = new AuthToken(user_id);
			Entity token = Entity.newBuilder(usrCurrentToken).set("usr_username", data.getUsername())
					.set("creation_data", g.toJson(at)).build();

			txn.put(user, userRole, userInfo, userPermission);
			txn.commit();

			UserData userData = this.userInfo.get(user_id);

			if (userData == null) {
				this.userInfo.put(user_id, data);
			}

			LOG.info("User resgisted " + data.getUsername() + " successfully");
			return Result.ok(at);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<AuthToken> login(LoginData data) {
		boolean validation_code = data.validation();
		if (validation_code) {
			return Result.error(Response.Status.BAD_REQUEST, "Invalid parameters");
		}

		LOG.fine("Login attempt by: " + data.getUsername());

		String user_id = data.getUsername();

		UserData userData = this.userInfo.get(user_id);

		// Verify if the user is in cache
		String hashedPassword = "";
		if (userData != null) {
			hashedPassword = userData.getPassword();
		} else {
			Key usrkey = userKeyFactory.newKey(user_id);
			//Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);

			//Get user by email or username
			/*Entity user = datastore.get(usrkey);
			

			if (user == null) {
				// User doesn't exist
				return Result.error(Response.Status.BAD_REQUEST, "User doens't exist");
			}
			hashedPassword = user.getString("usr_password");*/

			
			
			Query<Entity> query = null;
			QueryResults<Entity> result;
			
			if (data.getUsername().equals("UNDEFINED")) {
				query = Query.newEntityQueryBuilder().setKind("UserCredentials")
	                    .setFilter(PropertyFilter.eq("usr_email", data.getEmail()))
	                    .setLimit(5)
	                    .build();
			}
			
			if (data.getEmail().equals("UNDEFINED")) {
				query = Query.newEntityQueryBuilder().setKind("UserCredentials")
	                    .setFilter(PropertyFilter.eq("__key__", userKeyFactory.newKey(user_id)))
	                    .setLimit(5)
	                    .build();
			}
			
			if (query == null){
				return Result.error(Status.BAD_REQUEST, "");
			}
			
			result = datastore.run(query);
			if (result == null) {
				// User doesn't exist
				return Result.error(Response.Status.NOT_FOUND, "User " + data.getUsername() + " doens't exist");
			}
						
			hashedPassword =  result.next().getString("usr_password");

			
			
		}

		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
				// Correct password

				// Return token
				LOG.info("User " + data.getUsername() + "logged in successfully");
				AuthToken at = new AuthToken(user_id);
				
				this.userToken.put(at.tokenID, at);
				
				Entity token = Entity.newBuilder(usrCurrentToken).set("creation_data", g.toJson(at)).build();
				//Entity usrInfo = tnx.get(userInfoKey);

				tnx.put(token);
				tnx.commit();
				// return Response.ok(g.toJson(at)).build();
				return Result.ok(at);
			} else {
				LOG.warning("Wrong Password");
				tnx.rollback();
				// return Response.status(Status.FORBIDDEN).entity("Wrong password").build();
				return Result.error(Status.FORBIDDEN, "Wrong Password");
			}
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}

	}

	@Override
	public Result<Void> logout(AuthToken token) {

		LOG.fine("Logout attempt");
				
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token.username.trim());

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {

			Entity token_entity = tnx.get(usrCurrentToken);

			if (token_entity == null) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.BAD_REQUEST, "Token Invalid");
			}

			AuthToken token_creation = g.fromJson(token_entity.getString("creation_data"), AuthToken.class);

			if (!token.tokenID.equals(token_creation.tokenID)) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_ACCEPTABLE, "Token Invalid");
			}

			tnx.delete(usrCurrentToken);
			userToken.remove(token.tokenID);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<Void> promote(AuthToken token, String username, String new_role) {
		LOG.fine("Verification user " + username + "by " + token.username);

		// User to verify
		String user_id = username.trim();
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token.username.trim());

		// Higher priority user
		String high_user_id = token.username.trim();
		Key high_usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(high_user_id);
		Key high_usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(high_user_id);

		if (user_id.equals(high_user_id)) {
			LOG.warning("Same user");
			return Result.error(Status.BAD_REQUEST, "Same user");
		}

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user_verify = tnx.get(usrPermissionkey);
			Entity user_role = tnx.get(usrRoleKey);

			if (user_verify == null || user_role == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "User " + username + "doens't exist");
			}

			Entity token_entity = tnx.get(usrCurrentToken);

			if (token_entity == null) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.BAD_REQUEST, "Token invalid");
			}

			AuthToken token_creation = g.fromJson(token_entity.getString("creation_data"), AuthToken.class);
			long curr_time = System.currentTimeMillis();

			if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "Token invalid");
			}

			Entity high_user_verify = tnx.get(high_usrPermissionkey);
			Entity high_user_role = tnx.get(high_usrRoleKey);

			if (high_user_verify == null || high_user_role == null) {
				// Given higher priority User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "User " + token.username + "doens't exist");
			}

			UserRole role = UserRole.compareType(new_role);
			long high_role_priority = high_user_role.getLong("role_priority");

			if (high_role_priority <= user_role.getLong("role_priority") || role.getPriority() > high_role_priority) {
				tnx.rollback();
				LOG.warning("");
				return Result.error(Status.NOT_ACCEPTABLE, "");
			}

			// Super user not active
			if (!high_user_verify.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User " + token.username + " not active");
				return Result.error(Status.BAD_REQUEST, "User " + token.username + " not active");
			}

			// Success
			user_role = Entity.newBuilder(usrRoleKey).set("role_name", role.toString())
					.set("role_priority", role.getPriority()).build();

			tnx.put(user_role);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<String[]> getUser(String username) {
		String user_id = username.trim();
		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {
			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity userInfo = txn.get(userInfoKey);
			Entity userRole = txn.get(userRoleKey);
			Entity userPermission = txn.get(userPermissionKey);

			String[] response = { username, user.getString("usr_email"), userInfo.getString("usr_name"),
					userInfo.getString("usr_telephone"), userInfo.getString("usr_smartphone"),
					userInfo.getString("usr_address"), userInfo.getString("usr_NIF"), };

			return Result.ok(response);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<AuthToken> getToken(String username) {
		String user_id = username.trim();
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {
			Entity token = txn.get(usrCurrentToken);

			if (token == null) {
				txn.rollback();
				return Result.error(Status.BAD_REQUEST,"");
			}

			return Result.ok();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<Void> remove(AuthToken token, String username) {
		LOG.fine("Removing user " + username);

		String user_id = token.username.trim();
		String remove_id = username.trim();

		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);

		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		Key remove_userKey = userKeyFactory.newKey(remove_id);
		Key remove_userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(remove_id);
		Key remove_userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(remove_id);
		Key remove_userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(remove_id);
		Key remove_usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {
			// TODO Testing
			Entity token_entity = txn.get(usrCurrentToken);

			if (token_entity == null) {
				txn.rollback();
				LOG.fine("Username " + token.username + "not logged in");
				return Result.error(Status.NOT_ACCEPTABLE,"");
			}

			AuthToken token_creation = g.fromJson(token_entity.getString("creation_data"), AuthToken.class);
			long curr_time = System.currentTimeMillis();

			if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				txn.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}

			Entity userRole = txn.get(userRoleKey);

			if (user_id.equals(remove_id) && userRole.getLong("role_priority") <= 0) {

				Key userInfoKey = datastore.newKeyFactory().setKind("UserInfo").newKey(user_id);

				txn.delete(userKey, userRoleKey, userPermissionKey, userInfoKey, usrCurrentToken);
				txn.commit();
				return Result.ok();
			}

			Entity userPermission = txn.get(userPermissionKey);

			// Super user not active
			if (!userPermission.getString("usr_state").equals("ACTIVE")) {
				txn.rollback();
				LOG.warning("User " + token.username + " not active");
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity remove;
			Entity remove_userRole;

			remove = txn.get(remove_userKey);

			if (remove == null) {
				txn.rollback();
				LOG.fine("Username " + username + "doens't exists");
				return Result.error(Status.NOT_ACCEPTABLE, "");
			}

			remove_userRole = txn.get(remove_userRoleKey);

			if (userRole.getLong("role_priority") < remove_userRole.getLong("role_priority")) {
				txn.rollback();
				LOG.warning("User to be removed has higher role then this logged in user");
				return Result.error(Status.NOT_ACCEPTABLE, "");
			}

			txn.delete(remove_userKey, remove_userRoleKey, remove_userPermissionKey, remove_userInfoKey,
					remove_usrCurrentToken);
			txn.commit();
			return Result.ok();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<Void> activate(AuthToken token, String username) {
		LOG.fine("Verification user " + username + "by " + token.username);

		// Create key outside of this transaction
		// User to verify
		String user_id = username.trim();
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token.username.trim());

		// Higher priority user
		String high_user_id = token.username.trim();
		Key high_usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(high_user_id);
		Key high_usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(high_user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user_verify = tnx.get(usrPermissionkey);
			Entity user_role = tnx.get(usrRoleKey);

			if (user_verify == null || user_role == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity token_entity = tnx.get(usrCurrentToken);

			if (token_entity == null) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.BAD_REQUEST, "");
			}

			AuthToken token_creation = g.fromJson(token_entity.getString("creation_data"), AuthToken.class);
			long curr_time = System.currentTimeMillis();

			if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}

			Entity high_user_verify = tnx.get(high_usrPermissionkey);
			Entity high_user_role = tnx.get(high_usrRoleKey);

			if (high_user_verify == null || high_user_role == null) {
				// Given higher priority User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Key superUsrCredentialsKey = userKeyFactory.newKey(high_user_id);
			Entity superUsr = tnx.get(superUsrCredentialsKey);
			if (high_user_role.getLong("role_priority") <= user_role.getLong("role_priority")) {
				tnx.rollback();
				LOG.warning("");
				return Result.error(Status.NOT_ACCEPTABLE, "");
			}

			// Already active user
			if (user_verify.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User " + username + " already active");
				return Result.error(Status.BAD_REQUEST, "");
			}

			// Super user not active
			if (!high_user_verify.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User " + token.username + " not active");
				return Result.error(Status.BAD_REQUEST, "");
			}

			// Success
			String usrs_valid = token.username.trim();
			Entity userPermission = Entity.newBuilder(usrPermissionkey).set("usr_state", "ACTIVE")
					.set("list_usr_validation", usrs_valid).build();

			tnx.put(userPermission);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<Void> changePassword(AuthToken token, String new_password) {
		LOG.fine("Changing password");

		String user_id = token.username.trim();

		if (!user_id.equals(token.tokenID.trim())) {
			return Result.error(Status.BAD_REQUEST, "");
		}

		Key usrkey = userKeyFactory.newKey(user_id);
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token.username.trim());

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			if (new_password.trim().equals("")) {
				// New password is empty
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity user = tnx.get(usrkey);
			if (user == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity userToken = tnx.get(usrCurrentToken);
			if (userToken == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			AuthToken token_creation = g.fromJson(userToken.getString("creation_data"), AuthToken.class);
			long curr_time = System.currentTimeMillis();

			if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}

			Entity user_permission = tnx.get(usrPermissionkey);
			// Super user not active
			if (!user_permission.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User " + token.username + " not active");
				return Result.error(Status.BAD_REQUEST, "");
			}

			user = Entity.newBuilder(usrkey).set("usr_email", user.getString("usr_email"))
					.set("usr_password", DigestUtils.sha512Hex(new_password))
					// .set("usr_confirmation", DigestUtils.sha512Hex(data.getConfirmation()))
					.build();
			tnx.put(user);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<Void> changeAttributes(AuthToken token, String target_username, String list_json) {
		LOG.fine("Changing " + target_username + "'s aditional atributes");

		String user_id = token.username.trim();
		Key usrkey = userKeyFactory.newKey(user_id);
		Key usrKey_role = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key usrTokenKey = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		String target_user_id = target_username.trim();
		Key target_user_key = userKeyFactory.newKey(target_user_id);
		Key target_usrKey_role = datastore.newKeyFactory().setKind("UserRole").newKey(target_user_id);
		Key target_usrKey_info = datastore.newKeyFactory().setKind("UserPerfil").newKey(target_user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user = tnx.get(usrkey);
			if (user == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity userToken = tnx.get(usrTokenKey);
			if (userToken == null) {
				// User isn't login
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			AuthToken token_creation = g.fromJson(userToken.getString("creation_data"), AuthToken.class);
			long curr_time = System.currentTimeMillis();

			if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}

			String hashedPassword = user.getString("usr_password");
			if (!hashedPassword.equals(DigestUtils.sha512Hex(token.username))) {
				tnx.rollback();
				LOG.warning("Wrong parameters of " + token.username);
				return Result.error(Status.FORBIDDEN, "");
			}

			Entity target_user = tnx.get(target_user_key);
			if (target_user == null) {
				// Target user doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "");
			}

			Entity user_role = tnx.get(usrKey_role);
			Entity target_user_role = tnx.get(target_usrKey_role);

			if (!user_role.equals(target_user)
					&& target_user_role.getLong("role_priority") > user_role.getLong("role_priority")) {
				tnx.rollback();
				LOG.warning(target_username + "has higher role then " + token.username);
				return Result.error(Status.NOT_ACCEPTABLE, "");
			}

			Entity target = tnx.get(target_usrKey_info);
			final String[] list_att = { "usr_visibility", "usr_name", "usr_telephone", "usr_smartphone", "usr_address",
					"usr_NIF" };
			String[] attributes = g.fromJson(list_json, String[].class);
			for (int i = 0; i < attributes.length; i++) {
				attributes[i] = attributes[i].trim().equals("") ? target.getString(list_att[i]) : attributes[i];
			}

			target = Entity.newBuilder(target_usrKey_info).set("usr_visibility", attributes[0])
					.set("usr_name", attributes[1]).set("usr_telephone", attributes[2])
					.set("usr_smartphone", attributes[3]).set("usr_address", attributes[4])
					.set("usr_NIF", attributes[5]).build();

			tnx.put(target);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

}
