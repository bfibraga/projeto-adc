package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.RestUsers;

public class UsersResource implements RestUsers {

	private final UserImplementation impl = new UserImplementation();
	
}
