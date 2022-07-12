package pt.unl.fct.di.adc.silvanus.tests;

import org.testng.annotations.Test;
import pt.unl.fct.di.adc.silvanus.data.terrain.*;

public class TerrainDataTests {

    @Test
    public void test1(){
        TerrainData data = new TerrainData();

        assert !data.validation();
    }

    @Test
    public void test2(){
        TerrainData data = new TerrainData();

        assert !data.validation();

        data = new TerrainData(
                new LatLng[0],
                new TerrainIdentifierData(),
                new TerrainOwner(),
                new TerrainInfoData()
        );

        assert !data.validation();

        LatLng[] points = new LatLng[]{
                new LatLng(),
                new LatLng()
        };

        data = new TerrainData(
                points,
                new TerrainIdentifierData(
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno 1"
                ),
                new TerrainOwner(),
                new TerrainInfoData()
        );

        assert !data.validation();

        data = new TerrainData(
                points,
                new TerrainIdentifierData(
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno",
                        "Terreno",
                        "Terreno 1",
                        "Terreno 1"
                ),
                new TerrainOwner(),
                new TerrainInfoData()
        );

        assert !data.validation();

        data = new TerrainData(
                points,
                new TerrainIdentifierData(
                        "Terreno 1",
                        "Terreno 1",
                        "Terreno",
                        "Terreno",
                        "1",
                        "1"
                ),
                new TerrainOwner(),
                new TerrainInfoData()
        );

        assert data.validation();
    }
}
