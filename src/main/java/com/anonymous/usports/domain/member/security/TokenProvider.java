package com.anonymous.usports.domain.member.security;

import com.anonymous.usports.domain.member.dto.TokenDto;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import com.anonymous.usports.global.constant.TokenConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.redis.token.repository.TokenRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final MemberServiceImpl memberServiceImpl;
    private final TokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret.key}")
    private String secretKey;

    // 토큰 생성 매서드
    public String generateToken(String email, Long expireTime) {
        Claims claims = Jwts.claims().setSubject(email);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                .compact();
    }

    public TokenDto regenerateToken(String refreshToken){

        if(!validateToken(refreshToken)) {
            throw new JwtException(ErrorCode.JWT_EXPIRED.getDescription());
        }

        Claims claims = parseClaims(refreshToken);

        String email = claims.getSubject();

        String findToken = refreshTokenRepository.getToken(email);

        if (!refreshToken.equals(findToken)) {
            throw new JwtException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND.getDescription());
        }

        return saveTokenInRedis(email);
    }

    public TokenDto saveTokenInRedis(String email){
        String accessToken = generateToken(email, TokenConstant.ACCESS_TOKEN_VALID_TIME);

        String refreshToken = generateToken(email, TokenConstant.REFRESH_TOKEN_VALID_TIME);

        refreshTokenRepository.saveToken(refreshToken, email);

        return TokenDto.builder()
                .tokenType(TokenConstant.BEARER)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = memberServiceImpl.loadUserByUsername(getEmail(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        // 토큰이 빈 문자열이면 false
        if (!StringUtils.hasText(token)) return false;

        Claims claims = parseClaims(token);
        return !claims.getExpiration().before(new Date()); //토큰 만료 시간이 현재보다 이전인지 아닌지 만료 여부 확인
    }

    /**
     * access token이 redis denied map에 포함되었는지 확인
     */
    public boolean isAccessTokenDenied(String accessToken) {
        return refreshTokenRepository.existsBlackListAccessToken(accessToken);
    }

    /**
     * 헤더로 받은 값에서 PREFIX("Bearer ") 제거
     */
    public String resolveTokenFromRequest(String token) {
        if (StringUtils.hasText(token) && token.startsWith(TokenConstant.BEARER)) {
            return token.substring(TokenConstant.BEARER.length());
        }
        return null;
    }

    // 토큰이 유효한지 확인하는 메서드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            // 파싱하는 과정에서 토큰 만료 시간이 지날 수 있다, 만료된 토큰을 확인할 때에
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.JWT_EXPIRED.getDescription());
            // 토큰 형식에 문제가 있을 때
        } catch (SignatureException e) {
            throw new JwtException(ErrorCode.JWT_TOKEN_WRONG_TYPE.getDescription());
            // 토큰이 변조되었을 때
        } catch (MalformedJwtException e) {
            throw new JwtException(ErrorCode.JWT_TOKEN_MALFORMED.getDescription());
        }
    }

}
