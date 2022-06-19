package pt.unl.fct.di.adc.silvanus.data.user.auth;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TokenParams {
	private static final long EXPIRATION_TIME = 1000 * 60 * 20; // 20min
	private Set<String> scope;
	private long lvl_operation;

	public TokenParams(long lvl_operation, Set<String> scope) {
		this.lvl_operation = lvl_operation;
		this.scope = scope;
	}
	
	public TokenParams() {
		this.lvl_operation = 1;
		this.scope = new HashSet<>();
	}
}