package pt.unl.fct.di.adc.silvanus.data.user;

public class UserInfoData {

    private static final String DEFAULT_AVATAR = "https://storage.googleapis.com/projeto-adc.appspot.com/placeholder/avatar";

    private String name;
    private String visibility;
    private String nif;
    private String address;
    private String telephone;
    private String smartphone;

    private String avatar;

    public UserInfoData(){
        this("", "PUBLIC", "", "", "", "");
    }

    public UserInfoData(String name, String visibility, String nif, String address, String telephone, String smartphone){
        this.name = name;
        this.visibility = visibility.toUpperCase();
        this.nif = nif;
        this.address = address;
        this.telephone = telephone;
        this.smartphone = smartphone;
        this.avatar = DEFAULT_AVATAR;
    }

    public UserInfoData(String name, String visibility, String nif, String address, String telephone, String smartphone, String avatar){
        this.name = name;
        this.visibility = visibility.toUpperCase();
        this.nif = nif;
        this.address = address;
        this.telephone = telephone;
        this.smartphone = smartphone;
        this.avatar = avatar;
    }

    public String getName() {
        return this.name;
    }

    public UserInfoData setName(String name) {
        this.name = name;
        return this;
    }

    public UserInfoData replaceName(String name) {
        return !validField(this.getName()) && validField(name) ? this.setName(name) : this ;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public UserInfoData setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }

    public UserInfoData replaceVisibility(String visibility) {
        return !validVisibality(this.getVisibility()) && validField(visibility) ? this.setVisibility(visibility) : this ;
    }

    public String getNif() {
        return this.nif;
    }

    public UserInfoData setNif(String nif) {
        this.nif = nif;
        return this;
    }

    public UserInfoData replaceNIF(String nif) {
        return !validField(this.getNif()) && validField(nif) ? this.setNif(nif) : this ;
    }

    public String getAddress() {
        return this.address;
    }

    public UserInfoData setAddress(String address) {
        this.address = address;
        return this;
    }

    public UserInfoData replaceAddress(String address) {
        return !validField(this.getAddress()) && validField(address) ? this.setAddress(address) : this ;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public UserInfoData setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public UserInfoData replaceTelephone(String telephone) {
        return !validField(this.getTelephone()) && validField(telephone) ? this.setTelephone(telephone) : this ;
    }

    public String getSmartphone() {
        return this.smartphone;
    }

    public UserInfoData setSmartphone(String smartphone) {
        this.smartphone = smartphone;
        return this;
    }

    public UserInfoData replaceSmartphone(String smartphone) {
        return !validField(this.getSmartphone()) && validField(smartphone) ? this.setSmartphone(smartphone) : this ;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserInfoData setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public UserInfoData replaceAvatar(String avatar) {
        return !validField(this.getAvatar()) && validField(avatar) ? this.setAvatar(avatar) : this ;
    }

    /**
     * Checks if the given keyword is not equal to an empty string or null
     * @param keyword to validate
     * @return if the keyword is valid or not
     */
    private boolean validField(String keyword) {
        return keyword != null;
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
                validVisibality(this.getVisibility()) &&
                validField(this.getTelephone()) &&
                validField(this.getSmartphone());
    }

    @Override
    public String toString() {
        return String.format(
                "UserInfo:\n\tName:%s\n\tVisibility:%s\n\tNIF:%s\n\tAddress:%s\n\tTelephone:%s\n\tSmartphone:%s",
                this.getName(),
                this.getVisibility(),
                this.getNif(),
                this.getAddress(),
                this.getTelephone(),
                this.getSmartphone());
    }
}
