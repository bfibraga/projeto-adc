package pt.unl.fct.di.adc.silvanus.data.community;

import pt.unl.fct.di.adc.silvanus.api.impl.Community;

import java.util.Arrays;

public class CommunityData {
    private String name;
    private String responsible;
    private String[] members;

    public CommunityData(String name, String responsible, String[] members){
        this.name = name;
        this.responsible = responsible;
        this.members = members;
    }

    public CommunityData(){
        this("","",new String[]{});
    }

    public String getName() {
        return name;
    }

    public String getResponsible() {
        return responsible;
    }

    public String[] getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return String.format("CommunityData:\n\tName:%s\n\tResponsible:%s\n\tMembers:%s\n", this.getName(), this.getResponsible(), Arrays.toString(this.getMembers()));
    }
}
