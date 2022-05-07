package pt.unl.fct.di.adc.silvanus.util;

import javax.ws.rs.core.Response;

public class ResultOK<T> implements Result<T> {

	final T result;

	public ResultOK(T result) {
		this.result = result;
	}

	@Override
	public boolean isOK() {
		return true;
	}

	@Override
	public T value() {
		return result;
	}

	@Override
	public Response.Status error() {
		return Response.Status.OK;
	}

	public String toString() {
		return "(OK, " + value() + ")";
	}

}
