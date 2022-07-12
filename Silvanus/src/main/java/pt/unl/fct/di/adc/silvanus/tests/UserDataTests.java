package pt.unl.fct.di.adc.silvanus.tests;

import org.testng.annotations.Test;
import pt.unl.fct.di.adc.silvanus.data.user.LoginData;
import pt.unl.fct.di.adc.silvanus.data.user.UserData;
import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;

public class UserDataTests {

    @Test
    public void test1(){
        UserData data = new UserData();

        assert !data.validation();
    }

    @Test
    public void test2(){
        UserData data = new UserData();

        assert !data.validation();

        data = new UserData(
                new LoginData("teste", "teste@gmail.com", "teste12345"),
                "nao",
                new UserInfoData());

        assert !data.validation();

        data = new UserData(
                new LoginData("teste", "teste@gmail.com", "TeStE12345"),
                "teste12345",
                new UserInfoData());

        assert !data.validation();

        data = new UserData(
                new LoginData("teste", "teste@gmail.com", "TeStE"),
                "TeStE",
                new UserInfoData());

        assert !data.validation();

        data = new UserData(
                new LoginData("", "", "TeStE1"),
                "TeStE1",
                new UserInfoData());

        assert !data.validation();

        data = new UserData(
                new LoginData("teste", "teste", "TeStE1"),
                "TeStE1",
                new UserInfoData());

        assert !data.validation();
    }

    @Test
    public void test3(){
        UserData data = new UserData(
                new LoginData("teste", "teste@gmail.com", "TeSt1"),
                "TeSt1",
                new UserInfoData());

        assert data.validation();

        data = new UserData(
                new LoginData("teste", "teste@gmail.com", "teste12345"),
                "teste12345",
                new UserInfoData());

        assert data.validation();
    }
}
