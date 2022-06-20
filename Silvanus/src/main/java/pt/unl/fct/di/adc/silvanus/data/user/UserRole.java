package pt.unl.fct.di.adc.silvanus.data.user;

import pt.unl.fct.di.adc.silvanus.util.JSON;

public enum UserRole {
	USER("User", "#6aa84f"),
	FUNC_CONS("Funcionario Conselho", "#6fa8dc"),
	FUNC_DIST("Funcionario Distrito", "#c27ba0"),
	GOV("Governo", "#e69138"),
	ADMIN("Admin", "#cc0000");

	private String role_name;
	private String role_color;

	UserRole(String role_name, String role_color){
		this.role_name = role_name;
		this.role_color = role_color;
	}

	UserRole(){
		this("User", "#6aa84f");
	}

	public static UserRole compareType(String type) {
		for (UserRole r : UserRole.values()) {
			if (r.toString().equalsIgnoreCase(type)) {
				return r;
			}
		}
		return USER;
	}
	
	public String toString() {
		return String.format("%s:%s", this.getRoleName(), this.getRoleColor());
	}

	public String getRoleName(){
		return this.role_name;
	}

	public String getRoleColor(){
		return this.role_color;
	}
	
	public int getPriority() {
		return this.ordinal();
	}
}
