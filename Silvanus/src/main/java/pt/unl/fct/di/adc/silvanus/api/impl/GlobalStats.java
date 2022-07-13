package pt.unl.fct.di.adc.silvanus.api.impl;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.QueryResults;
import pt.unl.fct.di.adc.silvanus.data.global_stats.Stat;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.TerrainResultData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.List;

public interface GlobalStats {

    Result<List<Stat>> getStatsOnTypeOfTerrain();

    Result<List<Stat>> getStatsOnAreaOfTerrainOfUser(String userID);

    Result<Void> sendDataToDB();
}
