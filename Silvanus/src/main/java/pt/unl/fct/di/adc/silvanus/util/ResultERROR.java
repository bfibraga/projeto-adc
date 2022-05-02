package pt.unl.fct.di.adc.silvanus.util;

public class ResultERROR<T> implements Result<T> {

	final ErrorCode error;

	ResultERROR(ErrorCode error) {
		this.error = error;
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
	public ErrorCode error() {
		return error;
	}

	public String toString() {
		return "(" + error() + ")";
	}

}
