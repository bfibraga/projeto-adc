package pt.unl.fct.di.adc.silvanus.implementation;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserRole;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.api.Users;
import pt.unl.fct.di.adc.silvanus.util.cache.CacheManager;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.cache.UserCacheManager;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Logger;

public class UserImplementation implements Users {

	// Util classes
	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());
	private final Gson g;

	// Datastore
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = this.datastore.newKeyFactory().setKind("UserCredentials");

	//Cache
	private CacheManager<String> cache;

	public UserImplementation(){
		this.g = new Gson();
		this.cache = new UserCacheManager<>();
	}
	@Override
	public Result<String> register(UserData data) {
		LOG.fine("Register user " + data.getUsername());

		boolean validation_code = data.validation();
		if (validation_code) {
			LOG.warning("User " + data.getUsername() + "tryied to register with some empty important information");
			return Result.error(Response.Status.BAD_REQUEST, "User " + data.getUsername() + "tryied to register with some empty important information");
		}
		String user_id = data.getID();

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
			user = Entity.newBuilder(userKey)
					.set("usr_username", data.getUsername())
					.set("usr_email", data.getEmail())
					.set("usr_password", DigestUtils.sha512Hex(data.getPassword()))
					// .set("usr_confirmation", DigestUtils.sha512Hex(data.getConfirmation()))
					.build();
			this.cache.put(user_id, "credentials", user);

			// Role attribution
			UserRole role = UserRole.compareType(data.getRole());
			Entity userRole = Entity.newBuilder(userRoleKey)
					.set("role_name", role.toString())
					.set("role_priority", role.getPriority()).build();
			this.cache.put(user_id, "role", userRole
			);

			// Info of the new user
			Entity userInfo = Entity.newBuilder(userInfoKey)
					.set("usr_visibility", data.getVisibility())
					.set("usr_name", data.getName())
					.set("usr_telephone", data.getTelephone())
					.set("usr_smartphone", data.getSmartphone())
					.set("usr_address", data.getAddress())
					.set("usr_NIF", data.getNif())
					.build();
			this.cache.put(user_id, "info", userInfo);

			// Verification of this user
			String verified = "";
			Entity userPermission;
			userPermission = Entity.newBuilder(userPermissionKey)
					.set("usr_state", data.getState())
					.set("list_usr_validation", verified)
					.build();
			this.cache.put(user_id, "permission", userPermission);

			//TODO: First Registration information
			String jws = TOKEN.createNewJWS(user_id, 1);

			txn.put(user, userRole, userInfo, userPermission);
			txn.commit();

			LOG.info("User register " + data.getUsername() + " successfully");
			return Result.ok(jws);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<String> login(LoginData data) {
		boolean validation_code = data.validation();
		if (validation_code) {
			return Result.error(Response.Status.BAD_REQUEST, "Invalid parameters");
		}

		LOG.fine("Login attempt by: " + data.getUsername());

		String user_id = data.getID();

		//TODO refactor this cache part
		LoginData loginData = this.cache.get(user_id, "credentials", LoginData.class);

		// Verify if the user is in cache
		String hashedPassword = "";
		if (loginData != null)	{
			hashedPassword = loginData.getPassword();
			if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))){
				String jws = TOKEN.createNewJWS(user_id, 1);

				String refresh_token = TOKEN.newRefreshToken();

				return Result.ok(jws);
			} else {
				return Result.error(Status.FORBIDDEN, "Wrong Username or Password");
			}
		} else {
			Query<Entity> query = null;
			QueryResults<Entity> result;

			if (data.getUsername().equals(LoginData.NOT_DEFINED)) {
				query = Query.newEntityQueryBuilder()
						.setKind("UserCredentials")
						.setFilter(PropertyFilter.eq("usr_email", data.getEmail()))
						.setLimit(5)
						.build();
			}

			if (data.getEmail().equals(LoginData.NOT_DEFINED)) {
				query = Query.newEntityQueryBuilder()
						.setKind("UserCredentials")
						.setFilter(PropertyFilter.eq("usr_username", data.getUsername()))
						.setLimit(5)
						.build();
			}

			if (query == null){
				return Result.error(Status.BAD_REQUEST, "Something went wrong getting a list of users...");
			}

			result = datastore.run(query);
			if (result == null) {
				// User doesn't exist
				return Result.error(Response.Status.NOT_FOUND, "User " + data.getUsername() + " doens't exist");
			}

			while (result.hasNext()){
				Entity curr_result = result.next();
				hashedPassword = curr_result.getString("usr_password");

				if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
					// Correct password

					// Return token
					LOG.info("User " + data.getUsername() + "logged in successfully");

					String jws = TOKEN.createNewJWS(user_id, 1);

					String refresh_token = TOKEN.newRefreshToken();

					//Store in cache
					LoginData store_data = new LoginData(curr_result.getString("usr_username"),
							curr_result.getString("usr_email"),
							curr_result.getString("usr_password"));
					this.cache.put(user_id, "credentials", store_data);
					this.cache.put(user_id, "token", jws);

					return Result.ok(jws);
				}
			}

			LOG.warning("Wrong Password");
			return Result.error(Status.FORBIDDEN, "Wrong Username or Password");
		}
	}

	@Override
	public Result<Void> logout(String token) {

		LOG.fine("Logout attempt");

		if (token == null){
			return Result.error(Status.BAD_REQUEST, "Null token");
		}

		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		//Revoke token
		this.cache.remove(jws.getSubject(), "token");

		return Result.ok();
	}

	@Override
	public Result<Void> promote(String token, String username, String new_role) {

		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		String high_user_id = jws.getSubject();

		//Query to lookup for given user
		String user_id_promote = username;

		LOG.fine("Promotion of user " + username);

		// User to verify
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id_promote);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id_promote);

		// Higher priority user
		Key high_usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(high_user_id);
		Key high_usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(high_user_id);

		if (user_id_promote.equals(high_user_id)) {
			LOG.warning("Same user");
			return Result.error(Status.FORBIDDEN, "Same user");
		}

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user_verify = this.cache.get(user_id_promote, "permission", Entity.class);
			if (user_verify == null) user_verify = tnx.get(usrPermissionkey);

			Entity user_role = this.cache.get(user_id_promote, "role", Entity.class);
			if (user_role == null) user_role = tnx.get(usrRoleKey);

			if (user_verify == null || user_role == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "User " + username + "doesn't exist");
			}

			this.cache.put(user_id_promote, "permission", user_verify);
			this.cache.put(user_id_promote, "role", user_role);

			Entity high_user_verify = this.cache.get(high_user_id, "permission", Entity.class);
			if (high_user_verify == null) high_user_verify = tnx.get(high_usrPermissionkey);

			Entity high_user_role = this.cache.get(high_user_id, "role", Entity.class);
			if (high_user_role == null) high_user_role = tnx.get(high_usrRoleKey);

			if (high_user_verify == null || high_user_role == null) {
				// Given higher priority User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "User doens't exist");
			}

			this.cache.put(high_user_id, "permission", high_user_verify);
			this.cache.put(high_user_id, "role", high_user_role);

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
				LOG.warning("User not active");
				return Result.error(Status.BAD_REQUEST, "User not active");
			}

			// Success
			user_role = Entity.newBuilder(usrRoleKey)
					.set("role_name", role.toString())
					.set("role_priority", role.getPriority())
					.build();
			this.cache.put(user_id_promote, "role", user_role);

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
		//TODO Figure it out how to get userID
		String user_id = username.trim();

		//Get from DB
		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);

		Entity user = this.cache.get(user_id, "credentials", Entity.class);
		if (user == null) user = datastore.get(userKey);

		if (user == null) {
			return Result.error(Status.BAD_REQUEST, "");
		}

		Entity userInfo = this.cache.get(user_id, "info", Entity.class);
		if (userInfo == null) userInfo = datastore.get(userInfoKey);

		Entity userRole = this.cache.get(user_id, "role", Entity.class);
		if (userRole == null) userRole = datastore.get(userRoleKey);


		String[] response = {
				username,
				user.getString("usr_email"),
				userInfo.getString("usr_name"),
				userInfo.getString("usr_telephone"),
				userInfo.getString("usr_smartphone"),
				userInfo.getString("usr_address"),
				userInfo.getString("usr_NIF"),
				userRole.getString("role_name")
		};

		return Result.ok(response);
	}

	//TODO
	@Override
	public Result<String> refresh_token(String old_refresh_token) {
		return Result.ok(TOKEN.newRefreshToken());
	}

	@Override
	public Result<Void> remove(String token, String username) {
		LOG.fine("Removing user " + username);

		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		String user_id = jws.getId();

		//Query to lookup all users
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
			Entity userRole = this.cache.get(user_id, "role", Entity.class);
			if (userRole == null){
				userRole = txn.get(userRoleKey);
				this.cache.put(user_id, "role", userRole);
			}

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
				LOG.warning("User not active");
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
	public Result<Void> activate(String token, String username) {
		LOG.fine("Verification user " + username);

		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid token");
		}

		// Create key outside of this transaction
		// User to verify
		String user_id = username.trim();
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token);

		// Higher priority user
		String high_user_id = "";
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
				LOG.warning("User not active");
				return Result.error(Status.BAD_REQUEST, "");
			}

			// Success
			String usrs_valid = "";
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
	public Result<Void> changePassword(String token, String new_password) {
		LOG.fine("Changing password");

		String user_id = "";

		/*if (!user_id.equals(token.tokenID.trim())) {
			return Result.error(Status.BAD_REQUEST, "");
		}*/

		Key usrkey = userKeyFactory.newKey(user_id);
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token);

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

			/*if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}*/

			Entity user_permission = tnx.get(usrPermissionkey);
			// Super user not active
			if (!user_permission.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User not active");
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
	public Result<Void> changeAttributes(String token, String target_username, String list_json) {
		LOG.fine("Changing " + target_username + "'s aditional atributes");

		String user_id = "";
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

			/*if (!token.tokenID.equals(token_creation.tokenID) || curr_time > token.expirationData) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_FOUND, "");
			}*/

			/*String hashedPassword = user.getString("usr_password");
			if (!hashedPassword.equals(DigestUtils.sha512Hex(token.username))) {
				tnx.rollback();
				LOG.warning("Wrong parameters of " + token.username);
				return Result.error(Status.FORBIDDEN, "");
			}*/

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
				LOG.warning(target_username + "has higher role then ");
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
