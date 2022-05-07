package pt.unl.fct.di.adc.silvanus.implementation;

import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.silvanus.data.*;
import pt.unl.fct.di.adc.silvanus.util.*;

public class UserImplementation implements Users{

	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = this.datastore.newKeyFactory().setKind("UserCredentials");

	private final Gson g = new Gson();
	
	@Override
	public Result<AuthToken> register(RegisterData data) {
		LOG.fine("Resgiter user " + data.getUsername());

		boolean validation_code = data.validation();
		if (validation_code == false) {
			LOG.warning("User " + data.getUsername() + "tryied to register with some empty important information");
			return Result.error(Response.Status.BAD_REQUEST);
		}
		String user_id = data.getUsername().trim();

		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key userPermissionKey = datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		
		Key usrCurrentToken = datastore.newKeyFactory()
				.setKind("UserToken")
				.newKey(user_id);
		
		Transaction txn = datastore.newTransaction();

		try {
			// TODO Testing

			Entity user = txn.get(userKey);

			if (user != null) {
				txn.rollback();
				LOG.fine("Username " + data.getUsername() + "already exists\nTry again with another fancy nickname");
				/*return Response.status(Status.NOT_ACCEPTABLE)
						.entity("Username " + data.getUsername() + " already exists").build();*/
				return Result.error(Response.Status.BAD_REQUEST);
			}

			user = Entity.newBuilder(userKey)
					.set("usr_email", data.getEmail())
					.set("usr_password", DigestUtils.sha512Hex(data.getPassword()))
					// .set("usr_confirmation", DigestUtils.sha512Hex(data.getConfirmation()))
					.build();
			UserRole role = UserRole.compareType(data.getRole());

			Entity userRole = Entity.newBuilder(userRoleKey).set("role_name", role.toString())
					.set("role_priority", role.toString()).build();
			Entity userInfo = Entity.newBuilder(userInfoKey)
					.set("usr_visibility", data.getVisibility())
					.set("usr_name", data.getName())
					.set("usr_telephone", data.getTelephone())
					.set("usr_smartphone", data.getSmartphone())
					// .set("usr_address", data.getAddress())
					// .set("usr_NIF", data.getNIF())
					.build();
			
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
			Entity token = Entity.newBuilder(usrCurrentToken)
					.set("usr_username", data.getUsername())
					.set("creation_data", g.toJson(at))
					.build();
			

			txn.put(user, userRole, userInfo, userPermission);
			txn.commit();
			LOG.info("User resgisted " + data.getUsername() + " successfully");
			//return Response.ok(g.toJson(at)).entity("User resgisted " + data.getUsername() + " successfully").build();
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
		if (validation_code == false) {
			return Result.error(Response.Status.BAD_REQUEST);
		}

		LOG.fine("Login attempt by: " + data.getUsername());

		String user_id = data.getUsername().trim();
		Key usrkey = userKeyFactory.newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory()
				.setKind("UserToken")
				.newKey(user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user = tnx.get(usrkey);

			if (user == null) {
				// User doesn't exist
				tnx.rollback();
				//return Response.status(Status.BAD_REQUEST).entity("User " + data.getUsername() + " is not available").build();
				return Result.error(Response.Status.BAD_REQUEST);
			}

			String hashedPassword = user.getString("usr_password");
			if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
				// Correct password

				// Return token
				LOG.info("User " + data.getUsername() + "logged in successfully");
				AuthToken at = new AuthToken(user_id);
				Entity token = Entity.newBuilder(usrCurrentToken)
						.set("creation_data", g.toJson(at))
						.build();
				Entity usrInfo = tnx.get(userInfoKey);
				
				/*.set("usr_visibility", data.getVisibility())
					.set("usr_name", data.getName())
					.set("usr_telephone", data.getTelephone())
					.set("usr_smartphone", data.getSmartphone())*/
				
				tnx.put(token);
				tnx.commit();
				//return Response.ok(g.toJson(at)).build();
				return Result.ok(at);
			} else {
				LOG.warning("Wrong Password");
				tnx.rollback();
				//return Response.status(Status.FORBIDDEN).entity("Wrong password").build();
				return Result.error(Status.FORBIDDEN);
			}
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
	}

	@Override
	public Result<Void> logout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> promote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<RegisterData> getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<AuthToken> getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> activate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deactivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> changePassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> changeAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
