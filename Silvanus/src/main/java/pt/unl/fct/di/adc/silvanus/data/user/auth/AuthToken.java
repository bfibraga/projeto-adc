package pt.unl.fct.di.adc.silvanus.data.user.auth;

import java.util.UUID;

import com.google.rpc.context.AttributeContext.Auth;

public class AuthToken {
	public static final long EXPIRATION_TIME = 1000 * 60 * 20; // 20min
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;

	public AuthToken() {}
	
	public AuthToken(String username) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}
	
	public AuthToken(String username, String tokenID, long creationData, long expirationData) {
		this.username = username;
		this.tokenID = tokenID;
		this.creationData = creationData;
		this.expirationData = expirationData;
	}
}