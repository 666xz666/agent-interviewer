package com.agentpioneer.utils;

import jakarta.servlet.http.Cookie;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "yourSecretKey";
    private static final String COOKIE_NAME = "jwt_token";
    private static final long EXPIRATION_TIME = 86400000; // 24 小时
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET_KEY);
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    // 生成 JWT 并设置到 cookie
    public static void generateTokenAndSetCookie(Long userId, HttpServletResponse response) {
        String token = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);

        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    // 从 cookie 中解析 JWT 并提取 userId
    public static Long getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    try {
                        DecodedJWT jwt = VERIFIER.verify(token);
                        String userId = jwt.getSubject();
                        return Long.parseLong(userId);
                    } catch (JWTVerificationException | IllegalArgumentException e) {
                        // 解析失败，返回 null
                    }
                }
            }
        }
        return null;
    }
}