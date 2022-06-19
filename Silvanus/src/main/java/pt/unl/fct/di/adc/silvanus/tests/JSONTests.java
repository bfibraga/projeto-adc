package pt.unl.fct.di.adc.silvanus.tests;

import org.testng.annotations.Test;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.util.JSON;

public class JSONTests {

    @Test
    public void test1(){
        LoginData data = new LoginData("user1", "user1@gmail.com", "password");
        String data_json = JSON.encode(data);

        assert data_json != null;

        LoginData converted_data = JSON.decode(data_json, LoginData.class);

        assert converted_data.getUsername().equals(data.getUsername());
        assert converted_data.getEmail().equals(data.getEmail());
        assert converted_data.getPassword().equals(data.getPassword());
        assert converted_data.getID().equals(data.getID());

        converted_data.setEmail("email@email.com");

        assert !converted_data.getEmail().equals(data.getEmail());
    }
}
