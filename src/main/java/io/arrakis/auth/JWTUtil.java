package io.arrakis.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JWTUtil {

    private static final Algorithm algorithm = Algorithm.HMAC512("vk51gn5");

    public static String create(String username) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, 8600000);

        return JWT.create()
                .withExpiresAt(cal.getTime())
                .withIssuer("Gurney Halleck")
                .withAudience("Dune", "Arrakis", "Atreidis")
                .withSubject(username)
                .sign(algorithm);
    }

    public static String createWithExpiry(String username, long ttl) {
        return JWT.create()
                .withExpiresAt(Instant.now().plus(ttl, ChronoUnit.SECONDS))
                .withIssuer("Gurney Halleck")
                .withAudience("Dune", "Arrakis", "Atreidis")
                .withSubject(username)
                .sign(algorithm);
    }

    public static DecodedJWT decode(String jwt) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(jwt);
    }

    public static boolean validate(String jwt) {
        DecodedJWT decodedJWT = decode(jwt);
        return decodedJWT.getExpiresAt().after(new Date());
    }
}