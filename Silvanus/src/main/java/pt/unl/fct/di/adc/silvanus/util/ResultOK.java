package pt.unl.fct.di.adc.silvanus.util;

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
	public ErrorCode error() {
		return ErrorCode.OK;
	}

	public String toString() {
		return "(OK, " + value() + ")";
	}

}
