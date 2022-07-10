package pt.unl.fct.di.adc.silvanus.data.community;

import pt.unl.fct.di.adc.silvanus.api.impl.Community;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;

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
        this("","", new String[]{});
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

    public String getID(){
        return String.format("%s", this.getName().hashCode());
    }

    @Override
    public String toString() {
        return String.format("CommunityData:\n\tName:%s\n\tResponsible:%s\n\tMembers:%s\n", this.getName(), this.getResponsible(), Arrays.toString(this.getMembers()));
    }

    private boolean validField(String keyword) {
        return keyword !=null && !keyword.trim().equals("");
    }

    public boolean validation() {
        return validField(this.getName()) &&
                validField(this.getResponsible());
    }
}
