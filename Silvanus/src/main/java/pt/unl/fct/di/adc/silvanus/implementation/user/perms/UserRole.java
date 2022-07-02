package pt.unl.fct.di.adc.silvanus.implementation.user.perms;

public enum UserRole {
	HELPDESK("Helpdesk", "#202020"),
	ENDUSER("End-User", "#6aa84f"),
	FUNCCONS("Func-Cons", "#6fa8dc"),
	FUNCDIST("Func-Dist", "#e69138"),
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
			if (r.getRoleName().equalsIgnoreCase(type)) {
				return r;
			}
		}
		return ENDUSER;
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
