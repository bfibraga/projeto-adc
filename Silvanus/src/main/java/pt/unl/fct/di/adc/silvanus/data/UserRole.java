package pt.unl.fct.di.adc.silvanus.data;

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
	
	//TODO Verify integrity of the priority for each role
}
