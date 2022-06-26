package pt.unl.fct.di.adc.silvanus.util.result;

import javax.ws.rs.core.Response;

public interface Result<T> {
	/**
	 * Tests if the result is an error.
	 */
	boolean isOK();

	/**
	 * obtains the payload value of this result
	 * 
	 * @return the value of this result.
	 */
	T value();

	/**
	 *
	 * obtains the error code of this result
	 * 
	 * @return the error code
	 * 
	 */
	Response.Status error();
	
	/**
	 * 
	 * @return
	 */
	ResultMessage statusMessage();

	/**
	 * Convenience method for returning non error results of the given type
	 * 
	 * @return the value of the result
	 */
	static <T> ResultOK<T> ok(T result, String okMessage) {
		return new ResultOK<>(result, okMessage);
	}

	/**
	 * Convenience method for returning non error results without a value
	 * 
	 * @return non-error result
	 */
	static <T> ResultOK<T> ok() {
		return new ResultOK<>(null, "");
	}

	/**
	 * Convenience method used to return an error
	 * 
	 * @return
	 */
	static <T> ResultERROR<T> error(Response.Status error, String message) {
		return new ResultERROR<>(error, message);
	}
}
