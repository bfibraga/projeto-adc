package pt.unl.fct.di.adc.silvanus.implementation.user;

import java.util.*;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import io.jsonwebtoken.*;
import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;
import pt.unl.fct.di.adc.silvanus.data.user.*;
import pt.unl.fct.di.adc.silvanus.api.impl.Users;
import pt.unl.fct.di.adc.silvanus.data.user.perms.RoleCredentials;
import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInData;
import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInVisibleData;
import pt.unl.fct.di.adc.silvanus.data.user.result.LogoutData;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.implementation.user.perms.UserRole;
import pt.unl.fct.di.adc.silvanus.util.JSON;
import pt.unl.fct.di.adc.silvanus.util.Random;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.cache.UserCacheManager;

import pt.unl.fct.di.adc.silvanus.util.cripto.PASSWORD;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public class UserImplementation implements Users {

	// Util classes
	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());
	// Datastore
	private Datastore datastore;
	private KeyFactory userKeyFactory;
	private KeyFactory userRoleKeyFactory;
	private KeyFactory userPerfilKeyFactory;
	private KeyFactory userPermissionKeyFactory;
	private KeyFactory roleCredentialsKeyFactory;

	//Cache
	private UserCacheManager<String> cache = new UserCacheManager<>();

	public UserImplementation(){
		this.datastore = DatastoreOptions.getDefaultInstance().getService();
		this.userKeyFactory = datastore.newKeyFactory().setKind("UserCredentials");
		this.userRoleKeyFactory = datastore.newKeyFactory().setKind("UserRole");
		this.userPerfilKeyFactory = datastore.newKeyFactory().setKind("UserPerfil");
		this.userPermissionKeyFactory = datastore.newKeyFactory().setKind("UserPermission");
		this.roleCredentialsKeyFactory = datastore.newKeyFactory().setKind("RoleCredentials");
	}
	@Override
	public Result<String> register(UserData data) {
		long now = System.currentTimeMillis();
		System.out.println(data);
		LoginData loginData = data.getCredentials();
		boolean validation_code = data.validation();

		LOG.fine("Register user " + loginData.getUsername());

		if (!validation_code) {
			LOG.warning("User " + loginData.getUsername() + " tryied to register with some empty important information");
			return Result.error(Response.Status.BAD_REQUEST, "Alguns parametros vazios ou mal preenchidos");
		}
		String user_id = data.getID();

		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key logoutKey = datastore.newKeyFactory().setKind("UserLastLogout").newKey(user_id);
		Key roleCredentialsKey = roleCredentialsKeyFactory.newKey(data.getRole().toUpperCase());

		//Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);
		Key userKey = userKeyFactory.newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {
			// Verify if user exists
			QueryResults<Entity> result = this.find(loginData.getUsername(), "UserCredentials", "usr_username", 1);
			if (result.hasNext()){
				txn.rollback();
				LOG.fine("Username " + data.getCredentials().getUsername() + "already exists\nTry again with another fancy nickname");
				return Result.error(Response.Status.FORBIDDEN, "Username " + data.getCredentials().getUsername() + "already exists\nTry again with another fancy nickname");
			}

			result = this.find(loginData.getEmail(), "UserCredentials", "usr_email", 1);
			if (result.hasNext()) {
				txn.rollback();
				LOG.fine("Email " + data.getCredentials().getEmail() + "already exists\nTry again with another fancy email");
				return Result.error(Response.Status.FORBIDDEN, "Username " + data.getCredentials().getUsername() + "already exists\nTry again with another fancy nickname");
			}

			// Create a new User
			String encriptedPassword = PASSWORD.digest(loginData.getPassword());
			Entity user = Entity.newBuilder(userKey)
					.set("usr_username", loginData.getUsername())
					.set("usr_email", loginData.getEmail())
					.set("usr_password", encriptedPassword)
					.build();
			this.cache.put(user_id, loginData);

			// Role attribution
			System.out.println(data.getRole());
			UserRole role = UserRole.compareType(data.getRole());
			System.out.println(role.getRoleName());
			Entity userRole = Entity.newBuilder(userRoleKey)
					.set("role_name", role.getRoleName())
					.set("role_priority", role.getPriority()).build();
			this.cache.put(user_id, role);

			Entity roleCredentialsEntity = txn.get(roleCredentialsKey);
			RoleCredentials roleCredentials = new RoleCredentials(roleCredentialsEntity.getProperties());
			this.cache.put(user_id, "permissions", roleCredentials);

			// Info of the new user
			UserInfoData userInfoData = data.getInfo();
			Entity userInfo = Entity.newBuilder(userInfoKey)
					.set("usr_visibility", userInfoData.getVisibility())
					.set("usr_name", userInfoData.getName())
					.set("usr_telephone", userInfoData.getTelephone())
					.set("usr_smartphone", userInfoData.getSmartphone())
					.set("usr_address", userInfoData.getAddress())
					.set("usr_NIF", userInfoData.getNif())
					.build();
			this.cache.put(user_id, userInfoData);

			// Verification of this user
			String verified = JSON.encode(new HashSet<>());
			UserStateData userStateData = data.getUserStateData();
			Entity userPermission = Entity.newBuilder(userPermissionKey)
					.set("usr_state", userStateData.getSet())
					.set("list_usr_validation", verified)
					.set("confirm_code", Random.code())
					.build();
			this.cache.put(user_id, userStateData);

			Entity logoutEntity = Entity.newBuilder(logoutKey)
					.set("map_center_location", JSON.encode(new LatLng((float) 38.659784, (float) -9.202765)))
					.set("map_zoom", 15.0)
					.build();

			//TODO: First Registration information
			String jws = TOKEN.createNewJWS(user_id, 1, roleCredentials.getPermissions());

			txn.put(user, userRole, userInfo, userPermission);
			txn.commit();

			long time = System.currentTimeMillis() - now;

			LOG.info("User register " + loginData.getUsername() + " successfully: " + time + " miliseconds");
			return Result.ok(jws, "User register " + loginData.getUsername() + " successfully");
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<String> build(UserData data) {
		String user_id = data.getCredentials().getID();
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);

		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		Transaction txn = datastore.newTransaction();

		try {
			// Role attribution
			UserRole role = UserRole.compareType(data.getRole());
			Entity userRole = Entity.newBuilder(userRoleKey)
					.set("role_name", role.getRoleName())
					.set("role_priority", role.getPriority()).build();
			this.cache.put(user_id, role);

			// Info of the new user
			UserInfoData userInfoData = data.getInfo();
			Entity userInfo = Entity.newBuilder(userInfoKey)
					.set("usr_visibility", userInfoData.getVisibility())
					.set("usr_name", userInfoData.getName())
					.set("usr_telephone", userInfoData.getTelephone())
					.set("usr_smartphone", userInfoData.getSmartphone())
					.set("usr_address", userInfoData.getAddress())
					.set("usr_NIF", userInfoData.getNif())
					.build();
			this.cache.put(user_id, userInfoData);

			// Verification of this user
			String verified = JSON.encode(new HashSet<>());
			UserStateData userStateData = data.getUserStateData();
			Entity userPermission = Entity.newBuilder(userPermissionKey)
					.set("usr_state", userStateData.getSet())
					.set("list_usr_validation", verified)
					.build();
			this.cache.put(user_id, userStateData);

			txn.put(userRole, userInfo, userPermission);
			txn.commit();

			LOG.info("Build user " + user_id + " successfully");
			return Result.ok();
		} catch (Exception e){
			return Result.error(Status.INTERNAL_SERVER_ERROR, "Something went wrong with building " + data.getCredentials().getUsername() + "\n" + e.getMessage());
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Result<LoggedInData> login(LoginData data) {
		boolean validation_code = data.validation();
		if (validation_code) {
			return Result.error(Response.Status.BAD_REQUEST, "Invalid parameters");
		}

		LOG.fine("Login attempt by: " + data.getUsername());

		String user_id = data.getID();

		//TODO refactor this cache part
		RoleCredentials roleCredentials = this.cache.get(user_id, "permissions", RoleCredentials.class);
		LoginData loginData = this.cache.getLoginData(user_id);

		// Verify if the user is in cache
		String hashedPassword = "";
		if (loginData != null) {
			hashedPassword = loginData.getPassword();

			if (hashedPassword.equals(PASSWORD.digest(data.getPassword()))){
				if (roleCredentials == null){
					Key userRoleKey = userRoleKeyFactory.newKey(loginData.getID());

					Entity userRoleEntity = datastore.get(userRoleKey);

					Key roleCredentialsKey = roleCredentialsKeyFactory.newKey(userRoleEntity.getString("role_name").toUpperCase());

					Entity roleCredentialsEntity = datastore.get(roleCredentialsKey);

					roleCredentials = new RoleCredentials(roleCredentialsEntity.getProperties());

					this.cache.put(user_id, "permissions", roleCredentials);
				}

				String jws = TOKEN.createNewJWS(user_id, 1, roleCredentials.getPermissions());
				String refresh_token = TOKEN.newRefreshToken();

				UserStateData userStateData = this.cache.getStateData(loginData.getID());
				if (userStateData == null){
					Key permissionKey = userPermissionKeyFactory.newKey(loginData.getID());
					Entity permissionEntity = datastore.get(permissionKey);

					userStateData = new UserStateData(permissionEntity.getString("usr_state"),
							JSON.decode(permissionEntity.getString("list_usr_validation"), Set.class));

					this.cache.put(data.getID(), userStateData);
				}

				LoggedInData loggedInData = new LoggedInData(jws, userStateData);

				return Result.ok(loggedInData, "Login success");
			} else {
				LOG.warning("Wrong Password: Line 153");
				return Result.error(Status.FORBIDDEN, "Wrong Username or Password");
			}
		} else {
			QueryResults<Entity> result = this.find(data.getUsername(), "UserCredentials", "usr_username", 5);
			if (!result.hasNext()){
				result = this.find(data.getEmail(), "UserCredentials", "usr_email", 5);
			}

			while (result.hasNext()) {
				Entity curr_result = result.next();

				hashedPassword = curr_result.getString("usr_password");
				String givenPassword = PASSWORD.digest(data.getPassword());

				if (hashedPassword.equals(givenPassword)) {

					// Correct password
					// Return token
					LOG.info("User " + data.getUsername() + "logged in successfully");

					data = data.setUsername(curr_result.getString("usr_username"))
							.setEmail(curr_result.getString("usr_email"))
							.setPassword(givenPassword);

					if (roleCredentials == null){
						Key userRoleKey = userRoleKeyFactory.newKey(data.getID());

						Entity userRoleEntity = datastore.get(userRoleKey);

						Key roleCredentialsKey = roleCredentialsKeyFactory.newKey(userRoleEntity.getString("role_name").toUpperCase());

						Entity roleCredentialsEntity = datastore.get(roleCredentialsKey);

						roleCredentials = new RoleCredentials(roleCredentialsEntity.getProperties());

						this.cache.put(data.getID(), "permissions", roleCredentials);
					}

					int operation_level = 1;

					UserStateData userStateData = this.cache.getStateData(data.getID());
					if (userStateData == null){
						Key permissionKey = userPermissionKeyFactory.newKey(data.getID());
						Entity permissionEntity = datastore.get(permissionKey);

						userStateData = new UserStateData(permissionEntity.getString("usr_state"),
								JSON.decode(permissionEntity.getString("list_usr_validation"), Set.class));

						this.cache.put(data.getID(), userStateData);
					}

					String jws = TOKEN.createNewJWS(data.getID(), operation_level, roleCredentials.getPermissions());
					String refresh_token = TOKEN.newRefreshToken();

					LoggedInData loggedInData = new LoggedInData(jws, userStateData);

					//Store in cache
					this.cache.put(data.getID(), data);
					this.cache.put(data.getID(), "token:" + operation_level, jws);

					return Result.ok(loggedInData, "Login success");
				}
			}

			LOG.warning("Wrong Password");
			return Result.error(Status.FORBIDDEN, "Wrong Username or Password");
		}
	}

	private QueryResults<Entity> find(String identifier, String kind, String parameter, int limit_query){
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.eq(parameter, identifier))
				.setLimit(limit_query)
				.build();

		return datastore.run(query);
	}

	private QueryResults<Entity> find(String identifier, String kind, Key parameter, int limit_query){
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.eq(identifier, parameter))
				.setLimit(limit_query)
				.build();

		return datastore.run(query);
	}

	@Override
	public Result<Void> logout(String userID, LogoutData data) {

		LOG.fine("Logout attempt");

		//Create new Logout timestamp
		Key logoutKey = datastore.newKeyFactory().setKind("UserLastLogout").newKey(userID);

		Transaction txn = datastore.newTransaction();

		try{
			Entity logoutEntity = Entity.newBuilder(logoutKey)
					.set("time", LogoutData.fmt.format(new Date()))
					.set("map_center_location", JSON.encode(data.getCenter()))
					.set("map_zoom", data.getZoom())
					.build();

			txn.put(logoutEntity);
			txn.commit();
		}  finally {
			if (txn.isActive()){
				txn.rollback();
			}
		}

		//Revoke token
		this.cache.remove(userID, "token");

		return Result.ok();
	}

	//TODO TESTING
	@Override
	public Result<Void> promote(String token, String identifier, String new_role) {

		Claims jws = TOKEN.verifyToken(token);

		if (jws == null){
			return Result.error(Status.FORBIDDEN, "Invalid Token");
		}

		String high_user_id = jws.getSubject();
		System.out.println("High user id: " + high_user_id);
		System.out.println("Promotion on: " + identifier);
		String user_id_promote;
		LoginData loginData = this.cache.getLoginData(identifier);
		UserStateData userStateData = this.cache.getStateData(identifier);
		UserStateData highuserStateData = this.cache.getStateData(high_user_id);
		UserRole userRole = this.cache.getRoleData(identifier);
		UserRole highuserRole = this.cache.getRoleData(high_user_id);

		Key usrCredentialsKey = datastore.newKeyFactory().setKind("UserCredentials").newKey(identifier);
		Key usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(identifier);

		if (loginData == null || userStateData == null || highuserStateData == null || userRole == null || highuserRole == null){

			Entity curr_result = datastore.get(usrCredentialsKey);
			if (curr_result == null){
				LOG.info("User not found");
				return Result.error(Status.NOT_FOUND, "User not found");
			}
			user_id_promote = curr_result.getKey().getName();
			loginData = new LoginData(curr_result.getString("usr_username"),
					curr_result.getString("usr_email"),
					curr_result.getString("usr_password"));
			this.cache.put(identifier, loginData);

			loginData = new LoginData(curr_result.getString("usr_username"),
					curr_result.getString("usr_email"),
					curr_result.getString("usr_password"));
			this.cache.put(identifier, loginData);

			if (user_id_promote.equals(high_user_id)) {
				LOG.warning("Same user");
				return Result.error(Status.FORBIDDEN, "Same user");
			}

			LOG.fine("Promotion of user " + identifier);

			// User to verify
			Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id_promote);

			// Higher priority user
			Key high_usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(high_user_id);
			Key high_usrRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(high_user_id);

			Entity user_verify = datastore.get(usrPermissionkey);
			Entity user_role = datastore.get(usrRoleKey);

			if (user_verify == null || user_role == null) {
				// User doesn't exist
				return Result.error(Status.BAD_REQUEST, "User " + identifier + "doesn't exist");
			}

			userStateData = new UserStateData(user_verify.getString("usr_state"),
					JSON.decode(user_verify.getString("list_usr_validation"), String[].class));
			userRole = UserRole.compareType(user_role.getString("role_name"));

			this.cache.put(user_id_promote, userRole);
			this.cache.put(user_id_promote, userStateData);

			Entity high_user_verify = datastore.get(high_usrPermissionkey);
			Entity high_user_role = datastore.get(high_usrRoleKey);

			if (high_user_verify == null || high_user_role == null) {
				// Given higher priority User doesn't exist
				return Result.error(Status.BAD_REQUEST, "User doens't exist");
			}

			highuserStateData = new UserStateData(high_user_verify.getString("usr_state"),
					JSON.decode(high_user_verify.getString("list_usr_validation"), String[].class));
			highuserRole = UserRole.compareType(high_user_role.getString("role_name"));

			this.cache.put(high_user_id, highuserStateData);
			this.cache.put(high_user_id, highuserRole);
		}

		user_id_promote = loginData.getID();

		if (user_id_promote.equals(high_user_id)) {
			LOG.warning("Same user");
			return Result.error(Status.FORBIDDEN, "Same user");
		}

		UserRole role = UserRole.compareType(new_role);
		long high_role_priority = highuserRole.getPriority();

		if (high_role_priority <= userRole.getPriority() || role.getPriority() > high_role_priority) {
			LOG.warning("");
			return Result.error(Status.NOT_ACCEPTABLE, "");
		}

		// Super user not active
		if (!highuserStateData.getSet().equals("ACTIVE")) {
			LOG.warning("User not active");
			return Result.error(Status.BAD_REQUEST, "User not active");
		}

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			// Success
			Entity user_role = Entity.newBuilder(usrRoleKey)
					.set("role_name", role.toString())
					.set("role_priority", role.getPriority())
					.build();
			this.cache.put(user_id_promote, role);

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
	public Result<List<UserInfoVisible>> getUser(String request_user, String identifier) {

		//TODO Change key to remove some values when changing the values in other commands
		String key = identifier;
		String property = request_user;
		@SuppressWarnings("unchecked")
		List<UserInfoVisible> stored = this.cache.get(key, property, List.class);

		if (stored != null){
			System.out.println("Result in cache");
			return Result.ok(stored, "");
		}

		List<UserInfoVisible> result_set = new LinkedList<>();

		if (identifier.trim().equals("")){
			Key userKey = userKeyFactory.newKey(request_user);
			Entity user = datastore.get(userKey);
			UserInfoVisible info = this.getInfo(user);
			result_set.add(info);
			this.cache.put(request_user, property, result_set);
			return Result.ok(result_set, "");
		}

		//Query only for user identifiers
		//TODO Testing
		QueryResults<Entity> result = this.find(identifier, "UserCredentials", "usr_username", 5);
		if (!result.hasNext()){
			result = this.find(identifier, "UserCredentials", "usr_email", 5);
		}

		while(result.hasNext()){
			Entity curr_result = result.next();
			UserInfoVisible info = this.getInfo(curr_result);
			result_set.add(info);
			this.cache.put((String) curr_result.getKey().getNameOrId(), property, result_set);
		}


		return Result.ok(result_set, "");
	}

	private UserInfoVisible getInfo(Entity userEntity){
		String user_id = (String) userEntity.getKey().getNameOrId();

		System.out.println(user_id);
		Key infoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key stateKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		Key roleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key lastLogoutKey = datastore.newKeyFactory().setKind("UserLastLogout").newKey(user_id);

		Entity infoEntity = datastore.get(infoKey);
		Entity stateEntity = datastore.get(stateKey);
		Entity roleEntiry  = datastore.get(roleKey);
		Entity lastLogout = datastore.get(lastLogoutKey);

		UserInfoData info = new UserInfoData(
				infoEntity.getString("usr_name"),
				infoEntity.getString("usr_visibility"),
				infoEntity.getString("usr_NIF"),
				infoEntity.getString("usr_address"),
				infoEntity.getString("usr_telephone"),
				infoEntity.getString("usr_smartphone"),
				"");
		UserRole role = UserRole.compareType(roleEntiry.getString("role_name"));
		System.out.println(role);
		UserRoleData roleData = new UserRoleData(role.getDisplayName(), role.getRoleColor());
		System.out.println(roleData);
		LogoutData logoutData = lastLogout == null ?
				new LogoutData() :
				new LogoutData(
						lastLogout.getString("time"),
						JSON.decode(lastLogout.getString("map_center_location"), LatLng.class),
						lastLogout.getDouble("map_zoom"));

		//TODO Change the way to return result
		UserInfoVisible result = new UserInfoVisible(
				userEntity.getString("usr_username"),
				userEntity.getString("usr_email"),
				info,
				stateEntity.getString("usr_state"),
				role.getDisplayName(),
				role.getRoleColor(),
				logoutData,
				new LoggedInVisibleData(Arrays.asList(role.getMenus()))
		);

		return result;
	}

	//TODO
	@Override
	public Result<String> refresh_token(String old_refresh_token) {
		return Result.ok(TOKEN.newRefreshToken(), "");
	}

	@Override
	public Result<Void> remove(String userID, String identifier) {
		LOG.fine("Removing user " + identifier);

		if (identifier.trim().equals("")){
			//Try to remove his account
			Key remove_userKey = userKeyFactory.newKey(userID);
			Key remove_userRoleKey = userRoleKeyFactory.newKey(userID);
			Key remove_userPermissionKey = userPermissionKeyFactory.newKey(userID);
			Key remove_userInfoKey = userPerfilKeyFactory.newKey(userID);

			Transaction txn = datastore.newTransaction();

			try {
				//Has permission to remove himself?

				//Verify if this user is active

				txn.delete(remove_userKey, remove_userRoleKey, remove_userPermissionKey, remove_userInfoKey);
				txn.commit();

				this.cache.remove(userID);
				return Result.ok();
			} finally {
				if(txn.isActive()){
					txn.rollback();
				}
			}

		}

		String remove_id;

		QueryResults<Entity> result = this.find(identifier, "UserCredentials", "usr_username", 1);
		if (!result.hasNext()){
			result = this.find(identifier, "UserCredentials", "usr_email", 1);
		}

		if (!result.hasNext()){
			return Result.error(Status.NOT_FOUND, "User "+ identifier + " doens't exist");
		} else {
			remove_id = result.next().getKey().getName();
		}

		Key remove_userKey = userKeyFactory.newKey(remove_id);
		Key remove_userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(remove_id);
		Key remove_userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(remove_id);
		Key remove_userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(remove_id);

		Transaction txn = datastore.newTransaction();

		try {
			//Has permission to remove himself?

			//Verify if this user is active

			txn.delete(remove_userKey, remove_userRoleKey, remove_userPermissionKey, remove_userInfoKey);
			txn.commit();
			this.cache.remove(remove_id);
			return Result.ok();
		} finally {
			if(txn.isActive()){
				txn.rollback();
			}
		}

		//Query to lookup all users

		/*Key userKey = userKeyFactory.newKey(user_id);
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

			if (user_id.equals(remove_id)) {

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
				LOG.fine("Username " + identifier + "doens't exists");
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
		}*/
	}

	@Override
	public Result<Void> activate(String responsible, String identifier, String code) {

		if (identifier.trim().equals("")){
			return Result.error(Status.BAD_REQUEST, "Invalid parameter " + identifier);
		}

		if (code.trim().equals("") || code.length() < 8){
			return Result.error(Status.BAD_REQUEST, "Invalid parameter " + code);
		}

		LOG.fine("Verification user " + identifier);

		QueryResults<Entity> result = this.find(identifier, "UserCredentials", "usr_username", 1);
		if (!result.hasNext()){
			result = this.find(identifier, "UserCredentials", "usr_email", 1);
		}

		String activate_id = "";

		if (!result.hasNext()){
			return Result.error(Status.NOT_FOUND, "User "+ identifier + " doens't exist");
		} else {
			activate_id = result.next().getKey().getName();
		}

		Key userPermissionKey = userPermissionKeyFactory.newKey(activate_id);

		Transaction txn = datastore.newTransaction();

		try {
			Entity userPermissionEntity = txn.get(userPermissionKey);

			String confirm = userPermissionEntity.getString("confirm_code");
			if (confirm == null || confirm.equals(code)){
				Set<String> confirmed = JSON.decode(userPermissionEntity.getString("list_usr_validation"), Set.class);
				confirmed.add(responsible);
				userPermissionEntity = Entity.newBuilder(userPermissionEntity)
						.set("usr_state", "ACTIVE")
						.set("list_usr_validation", JSON.encode(confirmed))
						.set("confirm_code", code)
						.build();
				txn.put(userPermissionEntity);
				this.cache.remove(activate_id);
			} else {
				txn.rollback();
				return Result.error(Status.FORBIDDEN, "Wrong code. Try again");
			}
			txn.commit();
		} finally {
			if (txn.isActive()){
				txn.rollback();
			}
		}
		return Result.ok();
		/*// Create key outside of this transaction
		// User to verify
		String user_id = identifier.trim();
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
				LOG.warning("User " + identifier + " already active");
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
		}*/
	}

	@Override
	public Result<Void> changePassword(String userID, String new_password) {
		LOG.fine("Changing password");

		/*if (!user_id.equals(token.tokenID.trim())) {
			return Result.error(Status.BAD_REQUEST, "");
		}*/

		if (new_password.trim().equals("")) {
			// New password is empty
			return Result.error(Status.BAD_REQUEST, "Password not valid");
		}

		Key usrkey = userKeyFactory.newKey(userID);
		Key usrPermissionkey = datastore.newKeyFactory().setKind("UserPermission").newKey(userID);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {

			Entity user = tnx.get(usrkey);
			if (user == null) {
				// User doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, userID + " doens't exist");
			}

			Entity user_permission = tnx.get(usrPermissionkey);

			// User not active
			/*if (!user_permission.getString("usr_state").equals("ACTIVE")) {
				tnx.rollback();
				LOG.warning("User not active");
				return Result.error(Status.BAD_REQUEST, userID + " not active");
			}*/

			String value = PASSWORD.digest(new_password);
			String old_value = user.getString("usr_password");

			if (old_value.equals(new_password)){
				//Password are equal
				return Result.error(Status.BAD_REQUEST, "Both password are equal");
			}

			user = Entity.newBuilder(usrkey)
					.set("usr_username", user.getString("usr_username"))
					.set("usr_email", user.getString("usr_email"))
					.set("usr_password", value)
					// .set("usr_confirmation", DigestUtils.sha512Hex(data.getConfirmation()))
					.build();

			this.cache.remove(userID);
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
	public Result<UserInfoData> changeAttributes(String userID, String target_username, UserInfoData infoData) {
		LOG.fine("Changing " + target_username + "'s aditional atributes");

		/*if (!infoData.validation()){
			LOG.warning("Invalid parameter: " + target_username);
			return Result.error(Response.Status.BAD_REQUEST, "Invalid parameter: " + target_username);
		}*/

		Key usrkey = userKeyFactory.newKey(userID);
		Key usrKey_role = datastore.newKeyFactory().setKind("UserRole").newKey(userID);
		Key usrTokenKey = datastore.newKeyFactory().setKind("UserToken").newKey(userID);

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
				return Result.error(Status.BAD_REQUEST, userID + " doens't exist");
			}

			Entity target_user = tnx.get(target_user_key);
			if (target_user == null) {
				// Target user doesn't exist
				tnx.rollback();
				return Result.error(Status.BAD_REQUEST, target_user_id + " doens't exist");
			}

			Entity user_role = tnx.get(usrKey_role);
			Entity target_user_role = tnx.get(target_usrKey_role);

			if (!user_role.equals(target_user)
					&& target_user_role.getLong("role_priority") > user_role.getLong("role_priority")) {
				tnx.rollback();
				LOG.warning(target_username + "has higher role then " + userID);
				return Result.error(Status.NOT_ACCEPTABLE, target_username + "has higher role then " + userID);
			}

			Entity target = tnx.get(target_usrKey_info);

			infoData = infoData
					.replaceName(target.getString("usr_name"))
					.replaceVisibility(target.getString("usr_visibility"))
					.replaceAddress(target.getString("usr_address"))
					.replaceNIF(target.getString("usr_NIF"))
					.replaceTelephone(target.getString("usr_telephone"))
					.replaceSmartphone(target.getString("usr_smartphone"));

			target = Entity.newBuilder(target_usrKey_info)
					.set("usr_visibility", infoData.getVisibility())
					.set("usr_name", infoData.getName())
					.set("usr_telephone", infoData.getTelephone())
					.set("usr_smartphone", infoData.getSmartphone())
					.set("usr_address", infoData.getAddress())
					.set("usr_NIF", infoData.getNif())
					.build();

			this.cache.remove(target_username);
			System.out.println(infoData);

			tnx.put(target);
			tnx.commit();
			return Result.ok(infoData, "");
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}
}
