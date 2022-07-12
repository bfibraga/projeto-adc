package pt.unl.fct.di.adc.silvanus.data.community;

import pt.unl.fct.di.adc.silvanus.api.impl.Community;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunityData {
    private String name;
    private String responsible;
    private List<String> members;

    public CommunityData(String name, String responsible) {
        this.name = name;
        this.responsible = responsible;
        this.members = new ArrayList<>();
    }

    public CommunityData(String name, String responsible, List<String> members) {
        this.name = name;
        this.responsible = responsible;
        this.members = members;
    }

    public CommunityData() {
        this("", "");
    }

    public String getName() {
        return name;
    }

    public String getResponsible() {
        return responsible;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getID() {
        return String.format("%s", this.getName().hashCode());
    }

    public void setResponsible(String newResponsible) {
        this.responsible = newResponsible;
    }

    @Override
    public String toString() {
        StringBuilder allMembers = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            allMembers.append(members.get(i));
            if (i + 1 < members.size())
                allMembers.append(",");
        }
        return String.format("CommunityData:\n\tName:%s\n\tResponsible:%s\n\tMembers:%s\n", this.getName(), this.getResponsible(), allMembers);
    }

    private boolean validField(String keyword) {
        return keyword != null && !keyword.trim().equals("");
    }

    public boolean validation() {
        return validField(this.getName()) &&
                validField(this.getResponsible());
    }

    public void addMember(String newMember) {
        members.add(newMember);
    }
}
