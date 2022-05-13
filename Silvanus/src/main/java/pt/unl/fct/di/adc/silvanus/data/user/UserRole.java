package pt.unl.fct.di.adc.silvanus.data.user;

public enum UserRole {
	USER, GBO, GS, SU;
	
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
