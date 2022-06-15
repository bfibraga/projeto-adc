package pt.unl.fct.di.adc.silvanus.resources;

import com.google.cloud.datastore.Entity;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.implementation.ParcelImplementation;
import pt.unl.fct.di.adc.silvanus.util.Pair;
import pt.unl.fct.di.adc.silvanus.util.interfaces.RestParcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

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
        String nameTerrain = pair.getValue2();
        Result<Void> result = impl.approveTerrain(idOwner, nameTerrain);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }

    @Override
    public Response denyTerrain(Pair<String> pair) {
        String idOwner = pair.getValue1();
        String nameTerrain = pair.getValue2();
        Result<Void> result = impl.denyTerrain(idOwner, nameTerrain);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }

    @Override
    public Response deleteTerrain(String[] info) {
        String idOwner = info[0];
        String nameTerrain = info[1];
        Result<Void> result = impl.deleteTerrain(idOwner, nameTerrain);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result).build();
    }

    @Override
    public Response listTerrainUser(String idOfUser) {
        Result<List<String>> result = impl.getAllTerrainsOfUser(idOfUser);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainInCounty(String nameOfCounty) {
        Result<List<Entity>> result = impl.getAllTerrainsInCounty(nameOfCounty);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainInDistrict(String nameOfDistrict) {
        Result<List<Entity>> result = impl.getAllTerrainsInDistrict(nameOfDistrict);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }
}
