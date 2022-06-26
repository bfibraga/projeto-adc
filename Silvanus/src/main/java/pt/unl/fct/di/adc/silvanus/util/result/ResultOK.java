package pt.unl.fct.di.adc.silvanus.util.result;

import pt.unl.fct.di.adc.silvanus.util.JSON;

import javax.ws.rs.core.Response;

public class ResultOK<T> implements Result<T> {

	final T result;

	final String okMessage;

	public ResultOK(T result, String okMessage) {
		this.result = result;
		this.okMessage = okMessage;
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

	@Override
	public ResultMessage statusMessage() {
		return new ResultMessage(Response.Status.OK.getStatusCode(), okMessage);
	}

}
