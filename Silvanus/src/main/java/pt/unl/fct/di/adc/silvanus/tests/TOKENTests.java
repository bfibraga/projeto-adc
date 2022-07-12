package pt.unl.fct.di.adc.silvanus.tests;

import io.jsonwebtoken.Claims;
import org.testng.annotations.Test;
import pt.unl.fct.di.adc.silvanus.data.user.result.LoggedInData;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;

import java.util.ArrayList;
import java.util.HashSet;

public class TOKENTests {

    @Test
    public void test1(){
        String token1 = TOKEN.newRefreshToken();

        Claims claims = TOKEN.verifyToken(token1);

        assert claims != null;
        assert claims.getExpiration().getTime() >= System.currentTimeMillis();
        assert claims.getIssuedAt().getTime() <= System.currentTimeMillis();
        assert !claims.getId().equals("");

    }

    @Test
    public void test2(){
        String userID = "userID1";

        String token1 = TOKEN.createNewJWS(userID, 1, new HashSet<>());

        assert !token1.trim().equals("");

        String[] parts = token1.split("\\.");

        assert parts.length == 3;

        Claims claims = TOKEN.verifyToken(token1);

        assert claims != null;

        assert claims.getSubject().equals(userID);
    }
}
