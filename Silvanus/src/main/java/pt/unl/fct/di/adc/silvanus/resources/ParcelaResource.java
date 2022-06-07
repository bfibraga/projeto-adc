package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.implementation.ParcelImplementation;
import pt.unl.fct.di.adc.silvanus.util.Pair;
import pt.unl.fct.di.adc.silvanus.util.interfaces.RestParcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/parcela")
public class ParcelaResource implements RestParcel {

    private ParcelImplementation impl = new ParcelImplementation();

    public ParcelaResource() {
    }

    @Override
    public Response doRegister(TerrainData terrainData) {
        Result<Void> result = impl.createParcel(terrainData);

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response checkIfTerrainHasIntersections(Coordinate[] terrain) {
        Result<String> result = impl.checkIfParcelHasIntersections(terrain);
        if (result == null)
            return Response.ok("Nao ha intersecoes.").build();
        else
            return Response.ok("Foram detetadas intersecoes com a parcela:" + result.statusMessage()).build();
    }

    @Override
    public Response approveTerrain(Pair<String> pair) {
        String idOwner = pair.getValue1();
        String idParcel = pair.getValue2();
        Result<Void> result = impl.approveTerrain(idOwner, idParcel);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }

    @Override
    public Response denyTerrain(Pair<String> pair) {
        String idOwner = pair.getValue1();
        String idParcel = pair.getValue2();
        Result<Void> result = impl.denyTerrain(idOwner, idParcel);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }
}
