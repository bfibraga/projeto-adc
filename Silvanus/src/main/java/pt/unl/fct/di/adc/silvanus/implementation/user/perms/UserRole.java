package pt.unl.fct.di.adc.silvanus.implementation.user.perms;

public enum UserRole {
	HELPDESK("Helpdesk", "Helpdesk", "#202020"),
	ENDUSER("End-User", "Utilizador", "#6aa84f"),
	FUNCCONS("Func-Cons", "Funcionario Concelho %s", "#6fa8dc",
			"menu03",
			"menu04",
			"menu05"),
	FUNCDIST("Func-Dist", "Funcionario Distrito %s", "#e69138",
			"menu03",
			"menu04",
			"menu05"),
	GOV("GOV", "Funcionario Governo", "#007291",
			"menu03",
			"menu04",
			"menu05"),
	ADMIN("Admin", "Administrador", "#cd0000",
			"menu03",
			"menu04",
			"menu05");

	private String role_name;
	private String display_name;
	private String role_color;
	private String[] menus;

	UserRole(String role_name, String display_name, String role_color, String... menus){
		this.role_name = role_name;
		this.display_name = display_name;
		this.role_color = role_color;
		this.menus = menus;
	}

	UserRole(){
		this("End-User", "Utilizador", "#6aa84f");
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

	public String getDisplayName() {
		return display_name;
	}

	public String getRoleColor(){
		return this.role_color;
	}
	
	public int getPriority() {
		return this.ordinal();
	}

	public String[] getMenus() {
		return menus;
	}
}
