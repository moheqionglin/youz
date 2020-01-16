package com.sm.utils;

import com.alibaba.fastjson.JSONObject;
import com.sm.config.UserDetail;
import com.sm.dao.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * createAt: 2018/9/14
 */
@Component
public class JwtUtils {

    private static final String CLAIM_KEY_USER_ID = "user_id";
    private static final String CLAIM_KEY_AUTHORITIES = "roles";


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long access_token_expiration;

    @Value("${jwt.expiration}")
    private Long refresh_token_expiration;

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    public UserDetail getUserFromToken(String token) {
        UserDetail userDetail;
        try {
            final Claims claims = getClaimsFromToken(token);
            int userId = getUserIdFromToken(token);
            String username = claims.getSubject();
            String roleNames = claims.get(CLAIM_KEY_AUTHORITIES).toString();
            List<Role> roles = JSONObject.parseArray(roleNames).stream().map(r -> new Role(r.toString())).collect(Collectors.toList());
            userDetail = new UserDetail(userId, username, roles, "");
        } catch (Exception e) {
            userDetail = null;
        }
        return userDetail;
    }

    public int getUserIdFromToken(String token) {
        int userId;
        try {
            final Claims claims = getClaimsFromToken(token);
            userId = Integer.parseInt(String.valueOf(claims.get(CLAIM_KEY_USER_ID)));
        } catch (Exception e) {
            userId = 0;
        }
        return userId;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    /**
     * claims: userId, roles
     * subject: NickName
     *
     * @param userDetail
     * @return
     */
    public String generateAccessToken(UserDetail userDetail) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USER_ID, userDetail.getId());
        claims.put(CLAIM_KEY_AUTHORITIES, JSONObject.toJSONString(userDetail.getAuthorities().stream().map((GrantedAuthority a) -> a.getAuthority()).collect(Collectors.toList())));
        return generateAccessToken(userDetail.getUsername(), claims);
    }



    public Boolean validateToken(String token, UserDetails userDetails) {
        UserDetail userDetail = (UserDetail) userDetails;
        final int userId = getUserIdFromToken(token);
        return (userId == userDetail.getId()
                && !isTokenExpired(token)
        );
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration.before(new Date());
    }

    private String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, access_token_expiration);
    }


    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)//UserID，权限
                .setSubject(subject)//userName
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expiration))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact();
    }

}
