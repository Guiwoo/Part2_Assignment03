package com.guiwoo.stock_dividend.security;


import com.guiwoo.stock_dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("{spring.jwt.secret}")
    private String secretKey;

    private final static String KEY_ROLES = "roles";
    private final static long TOKEN_EXPIRED_TIME = 1000 * 60 * 60;
    private final MemberService memberService;

    /**
     * Token 생성 메서드
     * @param username 유저이름
     * @param roles 권한
     * @return
     */
    public String generateToken(String username, List<String> roles){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES,roles);
        Date now = new Date();
        Date expired = new Date(now.getTime() + TOKEN_EXPIRED_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS512,this.secretKey)
                .compact();
    }
    public String getUsername(String token){
        return this.parseClaims(token).getSubject();
    }
    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = memberService.loadUserByUsername(getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(
                userDetails,"",userDetails.getAuthorities());
    }
    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)) return false;
        Claims claims = this.parseClaims(token);
        return claims.getExpiration().before(new Date());
    }
    private Claims parseClaims(String token){
         try{
            return Jwts.parser()
                     .setSigningKey(secretKey)
                     .parseClaimsJws(token)
                     .getBody();
         }catch (ExpiredJwtException e){
             return e.getClaims();
         }
    }
}
