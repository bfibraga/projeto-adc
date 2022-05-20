package pt.unl.fct.di.adc.silvanus.util.interfaces;

import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Parcel {

	Result<Void> createParcel(ParcelaData dataParcela);
}
