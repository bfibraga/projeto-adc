package pt.unl.fct.di.adc.silvanus.data.parcel;

import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;

public class TerrainOwner {
    private String name;
    private String nif;
    private String address;
    private String telephone;
    private String smartphone;

    public TerrainOwner(){
        this("", "", "", "", "");
    }

    public TerrainOwner(String name, String nif, String address, String telephone, String smartphone){
        this.name = name;
        this.nif = nif;
        this.address = address;
        this.telephone = telephone;
        this.smartphone = smartphone;
    }

    public String getName() {
        return this.name;
    }

    public TerrainOwner setName(String name) {
        this.name = name;
        return this;
    }

    public TerrainOwner replaceName(String name) {
        return !validField(this.getName()) && validField(name) ? this.setName(name) : this ;
    }

    public String getNif() {
        return this.nif;
    }

    public TerrainOwner setNif(String nif) {
        this.nif = nif;
        return this;
    }

    public TerrainOwner replaceNIF(String nif) {
        return !validField(this.getNif()) && validField(nif) ? this.setNif(nif) : this ;
    }

    public String getAddress() {
        return this.address;
    }

    public TerrainOwner setAddress(String address) {
        this.address = address;
        return this;
    }

    public TerrainOwner replaceAddress(String address) {
        return !validField(this.getAddress()) && validField(address) ? this.setAddress(address) : this ;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public TerrainOwner setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public TerrainOwner replaceTelephone(String telephone) {
        return !validField(this.getTelephone()) && validField(telephone) ? this.setTelephone(telephone) : this ;
    }

    public String getSmartphone() {
        return this.smartphone;
    }

    public TerrainOwner setSmartphone(String smartphone) {
        this.smartphone = smartphone;
        return this;
    }

    public TerrainOwner replaceSmartphone(String smartphone) {
        return !validField(this.getSmartphone()) && validField(smartphone) ? this.setSmartphone(smartphone) : this ;
    }

    /**
     * Checks if the given keyword is not equal to an empty string or null
     * @param keyword to validate
     * @return if the keyword is valid or not
     */
    private boolean validField(String keyword) {
        return keyword != null && !keyword.trim().equals("");
    }

    /**
     * Checks if the given keyword is not equal to an empty string or null
     * @param keyword to validate
     * @return if the keyword is valid or not
     */
    private boolean validVisibality(String keyword) {
        return validField(keyword) && (keyword.trim().equalsIgnoreCase("PUBLIC") || keyword.trim().equalsIgnoreCase("PRIVATE"));
    }
    public boolean validation() {
        return validField(this.getName()) &&
                validField(this.getAddress()) &&
                validField(this.getNif()) &&
                validField(this.getTelephone()) &&
                validField(this.getSmartphone());
    }

    @Override
    public String toString() {
        return String.format(
                "UserInfo:\n\tName:%s\n\tNIF:%s\n\tAddress:%s\n\tTelephone:%s\n\tSmartphone:%s",
                this.getName(),
                this.getNif(),
                this.getAddress(),
                this.getTelephone(),
                this.getSmartphone());
    }
}
