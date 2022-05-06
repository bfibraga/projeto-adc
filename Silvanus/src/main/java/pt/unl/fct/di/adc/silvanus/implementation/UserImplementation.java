package pt.unl.fct.di.adc.silvanus.implementation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.silvanus.data.AuthToken;
import pt.unl.fct.di.adc.silvanus.data.LoginData;
import pt.unl.fct.di.adc.silvanus.data.UserData;
import pt.unl.fct.di.adc.silvanus.data.UserRole;
import pt.unl.fct.di.adc.silvanus.util.Result;
import pt.unl.fct.di.adc.silvanus.util.ResultOK;
import pt.unl.fct.di.adc.silvanus.util.Users;

public class UserImplementation implements Users {

	private static final Logger LOG = Logger.getLogger(UserImplementation.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = this.datastore.newKeyFactory().setKind("UserCredentials");

	private final Gson g = new Gson();
	
	public UserImplementation() {
		
	}

	@Override
	public Result<AuthToken> register(UserData data) {
		// TODO Auto-generated method stub
		LOG.fine("Resgiter user " + data.getUsername());

		if (data.validation()) {
			LOG.warning(
					"User: \"" + data.getUsername() + "\" tried to register with some empty important information.");
			return Result.error(400);
		}
		String user_id = data.getUsername().trim();

		Key userKey = userKeyFactory.newKey(user_id);
		Key userRoleKey = datastore.newKeyFactory().setKind("UserRole").newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		/*
		 * Key userPermissionKey =
		 * datastore.newKeyFactory().setKind("UserPermission").newKey(user_id);
		 * 
		 * Key usrCurrentToken =
		 * datastore.newKeyFactory().setKind("UserToken").newKey(user_id);
		 */

		Transaction txn = datastore.newTransaction();

		try {
			// TODO Testing

			Entity user = txn.get(userKey);

			if (user != null) {
				txn.rollback();
				LOG.fine("Username \"" + data.getUsername()
						+ "\" already exists\nTry again with another fancy nickname");
				return Result.error(404);
			}

			user = Entity.newBuilder(userKey).set("email", data.getEmail())
					.set("password", DigestUtils.sha512Hex(data.getPassword())).build();
			UserRole role = UserRole.compareType(data.getRole());

			Entity userRole = Entity.newBuilder(userRoleKey).set("role_name", role.toString()).build();

			Entity userInfo = Entity.newBuilder(userInfoKey).set("name", data.getName())
					.set("phone_number", data.getTelephone()).build();

			txn.put(user, userRole, userInfo);
			txn.commit();

			// TODO mudar isto para fazer uso das queries
			/*
			 * String verified = ""; Entity userPermission; if
			 * (role.toString().equals("SU")) { verified = data.getUsername().trim();
			 * userPermission = Entity.newBuilder(userPermissionKey).set("usr_state",
			 * "ACTIVE") .set("list_usr_validation", verified).build(); } else {
			 * userPermission = Entity.newBuilder(userPermissionKey).set("usr_state",
			 * data.getState()) .set("list_usr_validation", verified).build(); }
			 * 
			 * AuthToken at = new AuthToken(user_id); Entity token =
			 * Entity.newBuilder(usrCurrentToken).set("usr_username", data.getUsername())
			 * .set("creation_data", g.toJson(at)).build();
			 * 
			 * txn.put(user, userRole, userInfo, userPermission); txn.commit();
			 * LOG.info("User resgisted " + data.getUsername() + " successfully"); return
			 * Response.ok(g.toJson(at)).entity("User resgisted " + data.getUsername() +
			 * " successfully").build();
			 */
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		return null;
	}

	@Override
	public Result<AuthToken> login(LoginData data) {
		// TODO Auto-generated method stub

		if (!data.validation()) {
			LOG.warning(
					"User: \"" + data.getUsername() + "\" tried to register with some empty important information.");
			return Result.error(400);
		}

		LOG.fine("Login attempt by: " + data.getUsername());

		String user_id = data.getUsername().trim();
		Key usrkey = userKeyFactory.newKey(user_id);
		Key userInfoKey = datastore.newKeyFactory().setKind("UserPerfil").newKey(user_id);
		Key usrCurrentToken = datastore.newKeyFactory().setKind("UserToken").newKey(user_id);

		// Create new transation
		Transaction tnx = datastore.newTransaction();

		try {
			Entity user = tnx.get(usrkey);

			if (user == null) {
				// User doesn't exist
				tnx.rollback();
				LOG.warning("User: \"" + data.getUsername() + "\" is not available");
				return Result.error(404);
			}

			String hashedPassword = user.getString("usr_password");
			if (hashedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
				// Correct password

				// Return token
				LOG.info("User: \"" + data.getUsername() + "\" logged in successfully");
				AuthToken at = new AuthToken(user_id);
				Entity token = Entity.newBuilder(usrCurrentToken).set("creation_data", g.toJson(at)).build();
				//Entity usrInfo = tnx.get(userInfoKey);

				tnx.put(token);
				tnx.commit();
				Result<AuthToken> result = Result.ok(at);
				return result;
			} else {
				LOG.warning("Wrong Password");
				tnx.rollback();
			}
		} finally {
			if (tnx.isActive()) {
				tnx.rollback();
			}
		}
		return null;
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
	public Result<UserData> getUser() {
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
