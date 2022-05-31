package pt.unl.fct.di.adc.silvanus.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.unl.fct.di.adc.silvanus.data.parcel.Coordenada;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.implementation.ParcelImplementation;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

@Path("/parcela")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ParcelaResource {
	
	private ParcelImplementation impl = new ParcelImplementation();

	public ParcelaResource() {

	}

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegister(ParcelaData dataParcela) {
		Result<Void> result = impl.createParcel(dataParcela);
		
		if (!result.isOK()) {
			return Response.status(result.error()).build();
		}
		
		return Response.ok().build();
	}

	@POST
	@Path("/querie")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public void doQuerie(Coordenada[] parcela) {
		impl.quuéééééééééééééééééééééériiiiiiiiiisssssssssss(parcela);
	}
}
