package pt.unl.fct.di.adc.silvanus.data.user;

public class UserInfoData {

    private String name;
    private String visibility;
    private String nif;
    private String address;
    private String telephone;
    private String smartphone;

    public UserInfoData(){}

    public UserInfoData(String name, String visibility, String nif, String address, String telephone, String smartphone){
        this.name = name;
        this.visibility = visibility.toUpperCase();
        this.nif = nif;
        this.address = address;
        this.telephone = telephone;
        this.smartphone = smartphone;
    }

    public String getName() {
        return this.name;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public String getNif() {
        return this.nif;
    }

    public String getAddress() {
        return this.address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getSmartphone() {
        return this.smartphone;
    }
}
