package pt.unl.fct.di.adc.silvanus.implementation.user.perms;

import java.util.*;

public class UserPermsBuilder {

    private List<UserOps> scope;

    public UserPermsBuilder(){
        this.scope = new ArrayList<>();
    }

    public UserPermsBuilder add(UserOps op){
        this.scope.add(op);
        return this;
    }

    public int build(){
        int result = 0;
        for (UserOps op: scope) {
            result += op.bit();
        }
        return result;
    }
}
