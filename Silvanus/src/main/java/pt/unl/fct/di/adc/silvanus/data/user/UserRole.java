package pt.unl.fct.di.adc.silvanus.data.user;

public enum UserRole {
	USER("User", "#ff0000"),
	GBO("User", "#00ff00"),
	GS("User", "#0000ff"),
	SU("User", "#ffffff");

	private String role_name;
	private String role_color;
	private Permission[] permissions;

	UserRole(String role_name, String role_color, Permission... permissions){
		this.role_name = role_name;
		this.role_color = role_color;
		this.permissions = permissions;
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
		return this.name();
	}
	
	public int getPriority() {
		return this.ordinal();
	}
}
