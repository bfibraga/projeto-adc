package pt.unl.fct.di.adc.silvanus.implementation.global_stats;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.adc.silvanus.api.impl.GlobalStats;
import pt.unl.fct.di.adc.silvanus.data.global_stats.Stat;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.TerrainResultData;
import pt.unl.fct.di.adc.silvanus.implementation.terrain.TerrainImplementation;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalStatsImplementation implements GlobalStats {

    private final TerrainImplementation terrainImplementation = new TerrainImplementation();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private KeyFactory statsKeyFactory = datastore.newKeyFactory().setKind("Global Stats");

    private List<String> listOfTypes = new ArrayList<>();

    // TODO GlobalStatsResource e mandar para outro branch


    public GlobalStatsImplementation() {
        listOfTypes.add("Vazio");
        listOfTypes.add("Habitação");
        listOfTypes.add("Cultivo");
        listOfTypes.add("Empresarial/Adminstrativo");
        listOfTypes.add("Ensino");
        listOfTypes.add("Comércio");
        listOfTypes.add("Hospitalar");
        listOfTypes.add("Estrada/Infraestrutura");
    }

    @Override
    public Result<List<Stat>> getStatsOnCountyOfTerrains() {
        Result<List<TerrainResultData>> result = terrainImplementation.getAllApprovedTerrains();

        if (!result.isOK())
            return Result.error(result.error(), result.statusMessage().getMessage());

        List<TerrainResultData> terrains = result.value();

        int numberTerrains = 0;
        Map<String, Double> statMap = new HashMap<>();

        for (TerrainResultData terrain : terrains) {
            String type = terrain.getCredentials().getTownhall();

            if (statMap.containsKey(type)) {
                double value = statMap.get(type);
                statMap.put(type, value + 1);
            } else {
                statMap.put(type, 0.0);
            }

            numberTerrains++;
        }

        List<Stat> statList = new ArrayList<>();
        for (Map.Entry<String, Double> stat : statMap.entrySet()) {
            Stat tmp = new Stat(stat.getKey(), stat.getValue() / numberTerrains);
            statList.add(tmp);
        }
        return Result.ok(statList, "");
    }

    @Override
    public Result<List<Stat>> getStatsOnDistrictOfTerrains() {
        Result<List<TerrainResultData>> result = terrainImplementation.getAllApprovedTerrains();

        if (!result.isOK())
            return Result.error(result.error(), result.statusMessage().getMessage());

        List<TerrainResultData> terrains = result.value();

        int numberTerrains = 0;
        Map<String, Double> statMap = new HashMap<>();

        for (TerrainResultData terrain : terrains) {
            String type = terrain.getCredentials().getDistrict();

            if (statMap.containsKey(type)) {
                double value = statMap.get(type);
                statMap.put(type, value + 1);
            } else {
                statMap.put(type, 0.0);
            }

            numberTerrains++;
        }

        List<Stat> statList = new ArrayList<>();
        for (Map.Entry<String, Double> stat : statMap.entrySet()) {
            Stat tmp = new Stat(stat.getKey(), stat.getValue() / numberTerrains);
            statList.add(tmp);
        }
        return Result.ok(statList, "");
    }

    @Override
    public Result<List<Stat>> getStatsOnAreaOfTerrainOfUser(String userID) {
        Result<List<TerrainResultData>> result = terrainImplementation.getAllTerrainsOfUser(userID);

        if (!result.isOK())
            return Result.error(result.error(), result.statusMessage().getMessage());

        List<TerrainResultData> terrains = result.value();

        List<Stat> statsList = new ArrayList<>(terrains.size());

        for (TerrainResultData terrain : terrains) {
            String nameOfTerrain = terrain.getCredentials().getName();
            double areOfTerrain = terrain.getInfo().getArea();
            Stat tmp = new Stat(nameOfTerrain, areOfTerrain);
            statsList.add(tmp);
        }

        return Result.ok(statsList, "");
    }

    @Override
    public Result<Void> sendDataToDB() {
        Result<List<Stat>> result = getStatsOnCountyOfTerrains();
        List<Stat> terrains = result.value();
        Transaction txn = datastore.newTransaction();
        try {

            for (Stat stat : terrains) {
                Key k = statsKeyFactory.newKey(stat.getTopicOfStat());
                Entity tmp = Entity.newBuilder(k)
                        .set("topic", stat.getTopicOfStat())
                        .set("value", stat.getValueOfStat())
                        .build();

                txn.put(tmp);
            }

            txn.commit();
        } finally {
            if (txn.isActive())
                txn.rollback();
        }
        return Result.ok();
    }
}
