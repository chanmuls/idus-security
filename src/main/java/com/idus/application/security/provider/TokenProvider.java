package com.idus.application.security.provider;

import com.idus.application.response.TokenResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.core.exception.ApiException;
import com.idus.core.type.ServiceErrorType;
import com.idus.domain.user.entity.User;
import com.idus.domain.user.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    public static final String ISSUER = "IDUS";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    public static final String UID = "uid";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private UserService userService;

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponse generateToken(Authentication authentication) {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        TokenResponse tokenResponse = this.generateAccessToken(authentication);

        Claims claims = Jwts.claims();
        claims.setIssuer(ISSUER);
        claims.put(UID, ((PrincipalDetails) userDetails).getUser().getId());
        claims.setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME));

        String refreshToken = Jwts.builder()
                .setHeader(header)
                .setIssuer(claims.getIssuer())
                .setIssuedAt(claims.getIssuedAt())
                .setExpiration(claims.getExpiration())
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        tokenResponse.setRefreshToken(refreshToken);

        return tokenResponse;
    }

    public TokenResponse generateAccessToken(Authentication authentication) {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Claims claims = Jwts.claims();
        claims.setIssuer(ISSUER);
        claims.put(AUTHORITIES_KEY, authorities);
        claims.put(UID, ((PrincipalDetails) userDetails).getUser().getId());
        claims.setExpiration(accessTokenExpiresIn);

        String accessToken = Jwts.builder()
                .setHeader(header)
                .setIssuer(claims.getIssuer())
                .setIssuedAt(claims.getIssuedAt())
                .setExpiration(claims.getExpiration())
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new ApiException(ServiceErrorType.UNAUTHORIZED);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User user = userService.getById(Long.parseLong(String.valueOf(claims.get(UID))));

        UserDetails principal = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
