package pt.unl.fct.di.adc.silvanus.data.user.perms;

import com.google.cloud.datastore.Value;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleCredentials {

    private Set<String> permissions;

    public RoleCredentials(Map<String, Value<?>> permissions){
        this.permissions = new HashSet<>();
        for (Map.Entry<String, Value<?>> entry : permissions.entrySet()){
            //?
            System.out.println(entry.getValue().get());
            if (entry.getValue().get().equals(true)){
                this.permissions.add(entry.getKey());
            }
        }
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
