package pt.unl.fct.di.adc.silvanus.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.api.rest.RestParcels;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.implementation.ParcelImplementation;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

@Path(RestParcels.PATH)
public class ParcelaResource implements RestParcels {
	
	private ParcelImplementation impl = new ParcelImplementation();

	public ParcelaResource() {
	}

	@Override
	public Response doRegister(String token, ParcelaData dataParcela) {
		Result<Void> result = impl.createParcel(dataParcela);

		if (!result.isOK()) {
			return Response.status(result.error()).build();
		}

		return Response.ok().build();
	}
}
