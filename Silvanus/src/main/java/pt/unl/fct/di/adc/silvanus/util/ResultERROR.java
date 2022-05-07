package pt.unl.fct.di.adc.silvanus.util;

import javax.ws.rs.core.Response;

public class ResultERROR<T> implements Result<T> {
	final Response.Status error;
	final String response;

	ResultERROR(Response.Status error, String response) {
		this.error = error;
		this.response = response;
	}

	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public T value() {
		throw new RuntimeException("Attempting to extract the value of an Error: " + error());
	}

	@Override
	public Response.Status error() {
		return error;
	}
	
	public String response() {
		return response;
	}

	public String toString() {
		return "(" + error() + ")";
	}
}
