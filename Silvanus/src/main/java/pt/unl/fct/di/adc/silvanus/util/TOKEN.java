package pt.unl.fct.di.adc.silvanus.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.ws.rs.core.NewCookie;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * Helper class to implement JWT operations
 */
public class TOKEN {

    //TODO To be more secure, get secret key from database
    private static final String SECRET_KEY =
            "9aad93edcc3711da9ab40ed51da80bd0\n" +
            "99e700cc4505a36f48a9170f0d03e903\n" +
            "e8ad3aa8ad3f30470a4ba195a0f3474d\n" +
            "3eabf39a2d4001ee26e5250f279811b5\n" +
            "9290e1562d1ead23ddc244b3861f0f62\n" +
            "5508e0a48a7582d84f03bf45168cf91d\n" +
            "f673debfd41fdd66da134a1c5be490ef\n" +
            "c975f5b8ad057a5dbba94bb62f13c3dc\n" +
            "235dc36f243397a28d684575780cd6ad\n" +
            "4bae493f7c35f2998476a060187ebedd";

    private static final long DEFAULT_USER_EXPIRATION = 1000*60*60*12; //12h

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Create a new JSON Web Token from given userID.
     *
     * @param user_id - Given userID
     * @param operation_level - Level of the operation that can perform with this token
     * @return JWT for given user
     */
    public static String createNewJWS(String user_id, int operation_level){
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + (DEFAULT_USER_EXPIRATION)/operation_level);

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(String.valueOf(UUID.randomUUID()))
                .setSubject(user_id)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("op-level", operation_level)
                .signWith(key);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Verify the integrity of given JWT
     * @param token JWT in String format
     * @return Claims stored in that token
     */
    public static Claims verifyToken(String token){
        if (token == null || token.trim().equals("")){
            return null;
        }

        Claims jws;
        try{
            jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println(jws.getSubject());

            long curr_time = System.currentTimeMillis();
            long expirationData = jws.getExpiration().getTime();
            if (curr_time > expirationData) {
                return null;
            }
        } catch (JwtException e){
            System.out.println(e.getMessage());
            return null;
        }
        return jws;
    }

    /**
     * Create a new Refresh JSON Web Token from given userID with empty <b>important</b> info.
     * @return JWT for given user
     */
    public static String newRefreshToken(){
        Date creationDate = new Date();
        Date expirationDate = new Date(System.currentTimeMillis()+DEFAULT_USER_EXPIRATION*2*7);
        JwtBuilder refresh_token = Jwts.builder()
                .setExpiration(expirationDate) //a java.util.Date
                .setIssuedAt(creationDate) // for example, now
                .signWith(key)
                .setId(UUID.randomUUID().toString());
        return refresh_token.compact();
    }

    /**
     * Return User Token as a Cookie
     * @param value - Value of the token in String format
     * @return Cookie with value of the token
     */
    public static NewCookie cookie(String value){
        //TODO When this system is running on cloud, set secure parameter to true
        return new NewCookie("token", value, "/api", null, "Token of the user", 1000*60*60, false, true);
    }
}
