package pt.unl.fct.di.adc.silvanus.util.result;

import com.google.gson.Gson;
import pt.unl.fct.di.adc.silvanus.util.JSON;

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
		String[] fields = {this.value().toString(),this.statusMessage()};
		return JSON.encode(fields);
	}

	@Override
	public String statusMessage() {
		return "OK";
	}

}
