package pt.unl.fct.di.adc.silvanus.implementation.user.perms;

//TODO Rethink this part
public enum UserOps {

    //User Only Related
    LOGOUT,
    CHANGE_ATT,
    CHANGE_PWD,
    PROMOTE,
    LIST_USERS,
    REMOVE;
    //Parcel Related
    //Notification Related ????
    //Stats Related
    public int bitPos(){
        return this.ordinal();
    }

    public int bit() {
        int pos = this.bitPos();
        return (int) Math.pow(2, pos);
    }

    public boolean allowed(int perms){
        int binary = this.bit();
        return (binary & perms) != 0;
    }
}
