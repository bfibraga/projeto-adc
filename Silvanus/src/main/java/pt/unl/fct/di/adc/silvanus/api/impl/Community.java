package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Community {
    Result<CommunityData> list(String identifier);

    Result<Void> create(CommunityData data);

    Result<Void> delete(String name);

    Result<Void> join(String subject, String name);

    Result<Void> exit(String subject, String name);

    Result<Void> members(String subject, String name);
}
