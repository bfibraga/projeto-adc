package pt.unl.fct.di.adc.silvanus.data;

public enum UserRole {
	USER(0), GBO(1), GS(2), SU(3);
	
	private int priority;
	
	UserRole(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return this.priority;
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
	
	//TODO Verify integrity of the priority for each role
}
