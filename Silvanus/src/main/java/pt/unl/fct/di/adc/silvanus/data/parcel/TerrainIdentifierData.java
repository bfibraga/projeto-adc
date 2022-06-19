package pt.unl.fct.di.adc.silvanus.data.parcel;

public class TerrainIdentifierData {
    private String userID;
    private String name;
    private String townhall;
    private String district;
    private String section;
    private String number_article;

    public TerrainIdentifierData(){
        this("","","","","","");
    }

    public TerrainIdentifierData(
            String name,
            String townhall,
            String district,
            String section,
            String number_article
    ){
        this("", name, townhall,district, section, number_article);
    }

    public TerrainIdentifierData(
            String userID,
            String name,
            String townhall,
            String district,
            String section,
            String number_article
    ){
        this.userID = userID;
        this.name = name;
        this.townhall = townhall;
        this.district = district;
        this.section = section;
        this.number_article = number_article;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getTownhall() {
        return townhall;
    }

    public String getDistrict() {
        return district;
    }

    public String getSection() {
        return section;
    }

    public String getNumber_article() {
        return number_article;
    }

    public String getID(){
        return String.format("%s:%s", this.getUserID().hashCode(), this.getName().hashCode());
    }

    public TerrainIdentifierData setUserID(String userID){
        this.userID = userID;
        return this;
    }
}
