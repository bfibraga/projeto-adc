package pt.unl.fct.di.adc.silvanus.implementation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserRole;
import pt.unl.fct.di.adc.silvanus.data.user.auth.AuthToken;
import pt.unl.fct.di.adc.silvanus.util.*;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public class UserImplementation implements Users {

	// Util classes
	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());
	private final Gson g;

	private final SecretKey key;

	// Datastore
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = this.datastore.newKeyFactory().setKind("UserCredentials");

	// User info
	private ConcurrentHashMap<String, UserData> userInfo = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> userToken = new ConcurrentHashMap<>();

	public UserImplementation(){
		this.g = new Gson();
		this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}
	@Override
	public Result<String> register(UserData data) {
		LOG.fine("Resgiter user " + data.getUsername());

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

			// Role attribution
			UserRole role = UserRole.compareType(data.getRole());

			Entity userRole = Entity.newBuilder(userRoleKey)
					.set("role_name", role.toString())
					.set("role_priority", role.getPriority()).build();

			// Info of the new user
			Entity userInfo = Entity.newBuilder(userInfoKey)
					.set("usr_visibility", data.getVisibility())
					.set("usr_name", data.getName())
					.set("usr_telephone", data.getTelephone())
					.set("usr_smartphone", data.getSmartphone())
					.set("usr_address", data.getAddress())
					.set("usr_NIF", data.getNif())
					.build();

			// Verification of this user
			String verified = "";
			Entity userPermission;
			if (role.toString().equals("SU")) {
				verified = data.getUsername().trim();
				userPermission = Entity.newBuilder(userPermissionKey)
						.set("usr_state", "ACTIVE")
						.set("list_usr_validation", verified)
						.build();
			} else {
				userPermission = Entity.newBuilder(userPermissionKey)
						.set("usr_state", data.getState())
						.set("list_usr_validation", verified)
						.build();
			}

			//TODO: First Registration information
			String jws = this.createNewJWS(user_id);

			Entity token = Entity.newBuilder(usrCurrentToken)
					//.set("refresh_token", refresh_token)
					.set("jwt", jws)
					.build();

			txn.put(user, userRole, userInfo, userPermission);
			txn.commit();

			this.userInfo.putIfAbsent(user_id, data);

			LOG.info("User resgisted " + data.getUsername() + " successfully");
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

		UserData userData = this.userInfo.get(user_id);

		// Verify if the user is in cache
		String hashedPassword = "";
		if (userData != null) {
			hashedPassword = userData.getPassword();
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

			Key usrCurrentToken = datastore.newKeyFactory()
					.setKind("UserToken")
					.newKey(user_id);

			// Create new transation
			//Transaction tnx = datastore.newTransaction();

			try {
				while (result.hasNext()){
					hashedPassword =  result.next().getString("usr_password");

					if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
						// Correct password

						// Return token
						LOG.info("User " + data.getUsername() + "logged in successfully");

						String jws = this.createNewJWS(user_id);

						String refresh_token = this.newRefreshToken();
						//AuthToken at = new AuthToken(user_id);

						this.userToken.put(refresh_token, jws);

						/*Entity token = Entity.newBuilder(usrCurrentToken)
								//.set("refresh_token", refresh_token)
								.set("jwt", jws)
								.build();

						tnx.put(token);
						tnx.commit();*/
						return Result.ok(jws);
					}
				}

				LOG.warning("Wrong Password");
				//tnx.rollback();
				return Result.error(Status.FORBIDDEN, "Wrong Username or Password");
			} finally {
				/*if (tnx.isActive()) {
					tnx.rollback();
				}*/
			}
		}
		return Result.error(Status.BAD_REQUEST, "Oops");
	}

	@Override
	public Result<Void> logout(String token) {

		LOG.fine("Logout attempt");
		Claims jws = this.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		String user_id = jws.getId();

		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {

			Entity token_entity = tnx.get(usrCurrentToken);

			if (token_entity == null) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.BAD_REQUEST, "Token Invalid");
			}

			/*if (!token.tokenID.equals(token_creation.tokenID)) {
				tnx.rollback();
				LOG.warning("Token invalid");
				return Result.error(Status.NOT_ACCEPTABLE, "Token Invalid");
			}*/

			tnx.delete(usrCurrentToken);
			//userToken.remove(user_id);
			tnx.commit();
			return Result.ok();
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<Void> promote(String token, String username, String new_role) {

		Jws<Claims> jws;
		String jwsString = "";
		String user_id = username;
		String high_user_id = "";

		try{
			jws = Jwts.parserBuilder()  // (1)
					.setSigningKey(key)         // (2)
					.build()                    // (3)
					.parseClaimsJws(jwsString); // (4)

			user_id = jws.getBody().getId();
		} catch (JwtException e){
			return Result.error(Status.FORBIDDEN, "Invalid token");
		}

		long curr_time = System.currentTimeMillis();
		long expirationData = jws.getBody().getExpiration().getTime();
		if (curr_time > expirationData) {
			LOG.warning("Token invalid");
			return Result.error(Status.NOT_FOUND, "Token invalid");
		}

		LOG.fine("Promotion of user " + username);

		// User to verify
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(token);

		// Higher priority user
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

			Entity high_user_verify = tnx.get(high_usrPermissionkey);
			Entity high_user_role = tnx.get(high_usrRoleKey);

			if (high_user_verify == null || high_user_role == null) {
				// Given higher priority User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, "User doens't exist");
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
				LOG.warning("User not active");
				return Result.error(Status.BAD_REQUEST, "User not active");
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
			//Entity userPermission = txn.get(userPermissionKey);

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
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	//TODO
	@Override
	public Result<String> refresh_token(String old_refresh_token) {
		return Result.ok(this.newRefreshToken());
	}

	private String newRefreshToken(){
		Date creationDate = new Date();
		Date expirationDate = new Date(System.currentTimeMillis()+AuthToken.EXPIRATION_TIME);
		String refresh_token = Jwts.builder()
				.setExpiration(expirationDate) //a java.util.Date
				.setIssuedAt(creationDate) // for example, now
				.signWith(key)
				.setId(UUID.randomUUID().toString())
				.compact(); //just an example id
		return refresh_token;
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
	public Result<Void> remove(String token, String username) {
		LOG.fine("Removing user " + username);

		String jwsString = "";
		Claims jws = this.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		long curr_time = System.currentTimeMillis();
		long expirationData = jws.getExpiration().getTime();
		if (curr_time > expirationData) {
			LOG.warning("Token invalid");
			return Result.error(Status.NOT_FOUND, "Token invalid");
		}

		String user_id = jws.getId();
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
				LOG.fine("Username not logged in");
				return Result.error(Status.NOT_ACCEPTABLE,"");
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

		/*long curr_time = System.currentTimeMillis();
		long expirationData = jws.getBody().getExpiration().getTime();
		if (curr_time > expirationData) {
			LOG.warning("Token invalid");
			return Result.error(Status.NOT_FOUND, "Token invalid");
		}*/

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

	private String createNewJWS(String user_id){
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		Date expiration = new Date(nowMillis + AuthToken.EXPIRATION_TIME);

		//Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
				.setId(String.valueOf(UUID.randomUUID()))
				.setSubject(user_id)
				.setIssuedAt(now)
				.setExpiration(expiration)
				.signWith(key);

		//Builds the JWT and serializes it to a compact, URL-safe string
		//this.verifyToken(builder.compact());
		System.out.println(builder.compact());
		return builder.compact();
	}

	private Claims verifyToken(String jwsString){
		Claims jws = null;
		try{
			jws = Jwts.parserBuilder()
							.setSigningKey(key)
									.build()
											.parseClaimsJws(jwsString)
													.getBody();
			System.out.println(jws.getSubject());
		} catch (JwtException e){
			System.out.println(e.getMessage());
			return null;
		}
		return jws;
	}
}
