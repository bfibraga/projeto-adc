package pt.unl.fct.di.adc.silvanus.implementation.community;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import pt.unl.fct.di.adc.silvanus.api.impl.Community;
import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.implementation.user.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.JSON;
import pt.unl.fct.di.adc.silvanus.util.cache.CommunityCacheManager;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class CommunityImplementation implements Community {

    private static final String COMMUNITY_NAME = "name";
    private static final String COMMUNITY_RESPONSIBLE = "responsible";
    private static final String COMMUNITY_MEMBERS = "members";
    private static final String COMMUNITY_NUMBER_MEMBERS = "number_members";

    // Util classes
    private static final Logger LOG = Logger.getLogger(CommunityImplementation.class.getName());
    // Datastore
    private Datastore datastore;
    private KeyFactory communityKeyFactory;
    private KeyFactory communityMembersKeyFactory;


    private CommunityCacheManager<String> cache;

    public CommunityImplementation() {
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
        this.communityKeyFactory = datastore.newKeyFactory().setKind("Community");
        this.communityMembersKeyFactory = datastore.newKeyFactory().setKind("Members");

        this.cache = new CommunityCacheManager<>();
    }

    @Override
    public Result<List<CommunityData>> list() {
        Query<Entity> query;
        QueryResults<Entity> results;
        query = Query.newEntityQueryBuilder().setKind("Community").build();
        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "There are no communities.");

        List<CommunityData> list = new ArrayList<>();

        while (results.hasNext()) {
            Entity tmp = results.next();

            CommunityData communityData = null;

            System.out.println(tmp.getString(COMMUNITY_NAME));
            System.out.println(tmp.getString(COMMUNITY_RESPONSIBLE));

            // Since the community is always created with one element, it is dumb to check if it has zero elements. If it has, it is deleted.

            List<String> membersOfCommunity = members(tmp.getString(COMMUNITY_NAME)).value();

            communityData = new CommunityData(tmp.getString(COMMUNITY_NAME),
                    tmp.getString(COMMUNITY_RESPONSIBLE), membersOfCommunity);

            list.add(communityData);
        }

        return Result.ok(list, "");
    }

    @Override
    public Result<Void> create(CommunityData data) {
        if (!data.validation()) {
            return Result.error(Response.Status.BAD_REQUEST, "Data was not valid. Please check what you wrote.");
        }

        Key communityKey = communityKeyFactory.newKey(data.getID());

        if (datastore.get(communityKey) != null)
            return Result.error(Response.Status.NOT_FOUND, "The community already exists.");

        Transaction txn = datastore.newTransaction();

        try {
            Entity communityEntity = datastore.get(communityKey);

            if (communityEntity != null) {
                return Result.error(Response.Status.FORBIDDEN, data.getName() + " already exists");
            }

            communityEntity = Entity.newBuilder(communityKey)
                    .set(COMMUNITY_NAME, data.getName())
                    .set(COMMUNITY_RESPONSIBLE, data.getResponsible())
                    .set(COMMUNITY_NUMBER_MEMBERS, 1)
                    .build();

            txn.put(communityEntity);

            Key communityMemberKey = communityMembersKeyFactory.addAncestor(PathElement.of(
                    "Community", data.getID())).newKey(data.getResponsible());

            Entity member = Entity.newBuilder(communityMemberKey)
                    .set("id_of_user", data.getResponsible())
                    .set("name_of_community", data.getID())
                    .build();

            txn.put(member);


            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

        return Result.ok();
    }

    @Override
    public Result<Void> delete(String nameOfCommunity, String creator) {
        if (nameOfCommunity == null || nameOfCommunity.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Name is not valid.");

        Key communityKey = communityKeyFactory.newKey(String.format("%s", nameOfCommunity.hashCode()));

        Transaction txn = datastore.newTransaction();

        try {
            Entity community = datastore.get(communityKey);

            if (community == null)
                return Result.error(Response.Status.NOT_FOUND, "The community was not found.");

            if (!community.getString(COMMUNITY_RESPONSIBLE).equals(creator))
                return Result.error(Response.Status.FORBIDDEN, "This person can't delete this community.");

            // TODO apagar todos os users que estao numa comunidade

            Key k = communityMembersKeyFactory.addAncestor(PathElement.of(
                            "Community", String.format("%s", nameOfCommunity.hashCode())))
                    .newKey(datastore.get(communityKey).getString(COMMUNITY_RESPONSIBLE));


            Query<Entity> query;
            QueryResults<Entity> results;
            query = Query.newEntityQueryBuilder().setKind("Members").setFilter(
                    StructuredQuery.PropertyFilter.hasAncestor(k)).build();
            results = datastore.run(query);

            while (results.hasNext()) {
                Entity tmp = results.next();
                txn.delete(tmp.getKey());
            }

            txn.delete(communityKey);
            txn.commit();
        } finally {
            if (txn.isActive())
                txn.rollback();
        }
        return Result.ok();
    }

    @Override
    public Result<Void> join(String nameOfCommunity, String idOfUser) {
        if (nameOfCommunity == null || nameOfCommunity.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Name is not valid.");

        Key communityKey = communityKeyFactory.newKey(String.format("%s", nameOfCommunity.hashCode()));

        if (datastore.get(communityKey) == null)
            return Result.error(Response.Status.NOT_FOUND, "The community was not found.");

        Transaction txn = datastore.newTransaction();

        try {
            Entity community = datastore.get(communityKey);
            long value = community.getLong(COMMUNITY_NUMBER_MEMBERS);
            community = Entity.newBuilder(communityKey)
                    .set(COMMUNITY_NAME, community.getString(COMMUNITY_NAME))
                    .set(COMMUNITY_RESPONSIBLE, community.getString(COMMUNITY_RESPONSIBLE))
                    .set(COMMUNITY_NUMBER_MEMBERS, value + 1)
                    .build();

            txn.update(community);

            Key communityMemberKey = communityMembersKeyFactory.addAncestor(PathElement.of(
                            "Community", String.format("%s", nameOfCommunity.hashCode())))
                    .newKey(idOfUser);

            Entity member = Entity.newBuilder(communityMemberKey)
                    .set("id_of_user", idOfUser)
                    .set("name_of_community", nameOfCommunity.hashCode())
                    .build();

            txn.put(member);

            txn.commit();

        } finally {
            if (txn.isActive())
                txn.rollback();
        }
        return Result.ok();
    }

    @Override
    public Result<Void> exit(String nameOfCommunity, String idOfUser) {
        if (nameOfCommunity == null || nameOfCommunity.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Name is not valid.");

        Key communityKey = communityKeyFactory.newKey(String.format("%s", nameOfCommunity.hashCode()));

        if (datastore.get(communityKey) == null)
            return Result.error(Response.Status.NOT_FOUND, "The community was not found.");

        Query<Entity> query;
        QueryResults<Entity> results;
        query = Query.newEntityQueryBuilder().setKind("Members").setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.eq("name_of_community", nameOfCommunity.hashCode()),
                        StructuredQuery.PropertyFilter.eq("id_of_user", idOfUser)
                )
        ).build();
        results = datastore.run(query);
        if (!results.hasNext())
            return Result.error(Response.Status.BAD_REQUEST, "The user is not in the community.");

        Transaction txn = datastore.newTransaction();

        try {
            Entity tmp = results.next();
            txn.delete(tmp.getKey());

            Entity community = datastore.get(communityKey);

            long value = community.getLong(COMMUNITY_NUMBER_MEMBERS);
            if (value - 1 == 0)
                txn.delete(communityKey);

            else {

                community = Entity.newBuilder(communityKey)
                        .set(COMMUNITY_NAME, community.getString(COMMUNITY_NAME))
                        .set(COMMUNITY_RESPONSIBLE, community.getString(COMMUNITY_RESPONSIBLE))
                        .set(COMMUNITY_NUMBER_MEMBERS, value - 1)
                        .build();

                txn.update(community);

                Key communityMemberKey = communityMembersKeyFactory.addAncestor(PathElement.of(
                                "Community", String.format("%s", nameOfCommunity.hashCode())))
                        .newKey(idOfUser);

                txn.delete(communityMemberKey);
            }
            txn.commit();
        } finally {
            if (txn.isActive())
                txn.rollback();
        }

        return Result.ok();
    }

    @Override
    public Result<List<String>> members(String nameOfCommunity) {
        if (nameOfCommunity == null || nameOfCommunity.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Name is not valid.");


        Key communityKey = communityKeyFactory.newKey(String.format("%s", nameOfCommunity.hashCode()));

        if (datastore.get(communityKey) == null)
            return Result.error(Response.Status.NOT_FOUND, "The community was not found.");

        Key k = communityMembersKeyFactory.addAncestor(PathElement.of(
                        "Community", String.format("%s", nameOfCommunity.hashCode())))
                .newKey(datastore.get(communityKey).getString(COMMUNITY_RESPONSIBLE));


        Query<Entity> query;
        QueryResults<Entity> results;
        query = Query.newEntityQueryBuilder().setKind("Members").setFilter(
                StructuredQuery.PropertyFilter.hasAncestor(k)).build();

        results = datastore.run(query);

        List<String> elements = new ArrayList<>();

        while (results.hasNext()) {
            Entity tmp = results.next();
            elements.add(tmp.getString("id_of_user"));
        }

        return Result.ok(elements, "");
    }
/*
    public Result<Void> sendMessage(String userToSend, String messageToSend, String community) {
        if (community == null || community.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Name is not valid.");
        if (userToSend == null || userToSend.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "User is not valid.");
        if (messageToSend == null || messageToSend.isEmpty())
            return Result.error(Response.Status.NO_CONTENT, "Message is not valid.");


        Key communityKey = communityKeyFactory.newKey(String.format("%s", community.hashCode()));

        if (datastore.get(communityKey) == null)
            return Result.error(Response.Status.NOT_FOUND, "The community was not found.");

        long time = Timestamp.now().getSeconds();
    }

 */
}
