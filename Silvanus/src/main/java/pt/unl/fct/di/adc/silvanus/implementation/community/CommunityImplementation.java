package pt.unl.fct.di.adc.silvanus.implementation.community;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.adc.silvanus.api.impl.Community;
import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.implementation.user.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.JSON;
import pt.unl.fct.di.adc.silvanus.util.cache.CommunityCacheManager;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.logging.Logger;

public class CommunityImplementation implements Community {

    private static final String COMMUNITY_NAME = "name";
    private static final String COMMUNITY_RESPONSIBLE = "responsible";
    private static final String COMMUNITY_MEMBERS = "members";

    // Util classes
    private static final Logger LOG = Logger.getLogger(CommunityImplementation.class.getName());
    // Datastore
    private Datastore datastore;
    private KeyFactory communityKeyFactory;
    private KeyFactory communityMembersKeyFactory;


    private CommunityCacheManager<String> cache;

    public CommunityImplementation(){
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
        this.communityKeyFactory = datastore.newKeyFactory().setKind("Community");
        this.communityMembersKeyFactory = datastore.newKeyFactory().setKind("Members");

        this.cache = new CommunityCacheManager<>();
    }
    @Override
    public Result<CommunityData> list(String identifier) {
        return Result.ok();
    }

    //TODO Testing
    @Override
    public Result<Void> create(CommunityData data) {
        if (!data.validation()){
            return Result.error(Response.Status.BAD_REQUEST, "Bad forming data");
        }

        Key communityKey = communityKeyFactory.newKey(data.getID());

        String[] members = data.getMembers();
        Key[] communityMembersKey = new Key[members.length]; //=

        for (int i = 0; i < members.length; i++) {
            String member = members[i];
            communityMembersKey[i] = communityMembersKeyFactory.addAncestor(PathElement.of("Community", data.getID())).newKey(member);
        }

        Transaction txn = datastore.newTransaction();

        try{
            Entity communityEntity = datastore.get(communityKey);

            if (communityEntity != null){
                return Result.error(Response.Status.FORBIDDEN, data.getName() + " already exists");
            }

            communityEntity = Entity.newBuilder(communityKey)
                    .set(COMMUNITY_NAME, data.getName())
                    .set(COMMUNITY_RESPONSIBLE, data.getResponsible())
                    .build();

            Entity[] communityMemberEntities = new Entity[communityMembersKey.length];

            for (int i = 0; i < communityMemberEntities.length; i++) {
                communityMemberEntities[i] = Entity.newBuilder(communityMembersKey[i])
                        .set("joined_time", new Date().getTime())
                        .build();
                txn.put(communityMemberEntities[i]);
            }

            txn.put(communityEntity);
            txn.commit();
            txn.rollback();
        } finally {
            if (txn.isActive()){
                txn.rollback();
            }
        }

        return Result.ok();
    }

    @Override
    public Result<Void> delete(String name) {
        return Result.ok();
    }

    @Override
    public Result<Void> join(String subject, String name) {
        return Result.ok();
    }

    @Override
    public Result<Void> exit(String subject, String name) {
        return Result.ok();
    }

    @Override
    public Result<Void> members(String subject, String name) {
        return Result.ok();
    }
}
