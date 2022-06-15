package pt.unl.fct.di.adc.silvanus.api.impl;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Parcel {

	Result<Void> createParcel(ParcelaData dataParcela) throws JsonProcessingException;
}
