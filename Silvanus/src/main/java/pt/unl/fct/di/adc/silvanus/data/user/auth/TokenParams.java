package pt.unl.fct.di.adc.silvanus.data.user.auth;

import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenParams {
	private List<String> scope;
	private long lvl_operation;

	public TokenParams(long lvl_operation, List<String> scope) {
		this.lvl_operation = lvl_operation;
		this.scope = scope;
	}
	
	public TokenParams() {
		this.lvl_operation = 1;
		this.scope = new ArrayList<>();
	}

	public List<String> getScope() {
		return scope;
	}

	public long getLvl_operation() {
		return lvl_operation;
	}
}