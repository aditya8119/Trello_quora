package com.upgrad.quora.service.business;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.common.UnexpectedException;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.UUID;

/**
 * This class is used in the project to provide JWT token after successful authentication
 */
public class JwtTokenProvider {
    private static final String TOKEN_ISSUER = "https://quora.io";

    private final Algorithm algorithm;

    /**
     * A constructor for JwtTokenProvider class which receives user password as an argument to be used in the signature part of JWT access token.
     */
    public JwtTokenProvider(final String secret) {
        try {
            algorithm = Algorithm.HMAC512(secret);
        } catch (IllegalArgumentException e) {
            throw new UnexpectedException(GenericErrorCode.GEN_001);
        }
    }


    /**
     * This method receives uuid of the user, current time and expiry time of the access token.
     * This information is stored in the payload of the JWT token and the JWT token is returned by this method.
     */
    /**
     * @param userUuid        - uuid of the user
     * @param issuedDateTime  - current time
     * @param expiresDateTime - expiry time of the JWT token
     * @return - generated JWT token
     */
    public String generateToken(final String userUuid, final ZonedDateTime issuedDateTime, final ZonedDateTime expiresDateTime) {

        final Date issuedAt = new Date(issuedDateTime.getLong(ChronoField.INSTANT_SECONDS));
        final Date expiresAt = new Date(expiresDateTime.getLong(ChronoField.INSTANT_SECONDS));

        return JWT.create().withIssuer(TOKEN_ISSUER) //
                .withKeyId(UUID.randomUUID().toString())
                .withAudience(userUuid) //
                .withIssuedAt(issuedAt).withExpiresAt(expiresAt).sign(algorithm);
    }

}
