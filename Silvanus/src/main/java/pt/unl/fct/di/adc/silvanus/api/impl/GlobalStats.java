package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.global_stats.Stat;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.List;

public interface GlobalStats {

    Result<List<Stat>> getStatsOnCountyOfTerrains();

    Result<List<Stat>> getStatsOnAreaOfTerrainOfUser(String userID);

    Result<Void> sendDataToDB();

    Result<List<Stat>> getStatsOnDistrictOfTerrains();
}
