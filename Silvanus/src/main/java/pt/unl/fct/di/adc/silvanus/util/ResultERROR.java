package pt.unl.fct.di.adc.silvanus.util;

public class ResultERROR<T> implements Result<T> {

	final int status;

	ResultERROR(int error) {
		this.status = error;
	}

	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public T value() {
		return null;
	}

	@Override
	public Integer status() {
		return status;
	}

	public String toString() {
		return "(" + this.status() + ")";
	}

}
