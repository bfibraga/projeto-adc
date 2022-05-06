package pt.unl.fct.di.adc.silvanus.util;

import javax.ws.rs.core.Response;

public class ResultOK<T> implements Result<T> {

	private final T result;

	public ResultOK(T result) {
		this.result = result;
	}

	public boolean isOK() {
		return true;
	}

	public T value() {
		return result;
	}


	public String toString() {
		return "(OK, " + value() + ")";
	}

	@Override
	public Integer status() {
		return Response.Status.OK.getStatusCode();
	}

}
