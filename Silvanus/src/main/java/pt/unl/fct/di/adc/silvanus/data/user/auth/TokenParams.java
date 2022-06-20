package pt.unl.fct.di.adc.silvanus.data.user.auth;

import java.util.HashSet;
import java.util.Set;

public class TokenParams {
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

	public Set<String> getScope() {
		return scope;
	}

	public long getLvl_operation() {
		return lvl_operation;
	}
}