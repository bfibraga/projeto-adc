package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.List;

public interface Community {
    Result<List<CommunityData>> list();

    Result<Void> create(CommunityData data);

    Result<Void> delete(String nameOfCommunity, String creator);

    Result<Void> join(String nameOfCommunity, String idOfUser);

    Result<Void> exit(String nameOfCommunity, String idOfUser);

    Result<List<String>> members(String nameOfCommunity);
}
