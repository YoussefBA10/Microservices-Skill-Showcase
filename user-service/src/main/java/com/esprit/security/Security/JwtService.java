package com.esprit.security.Security;

import com.esprit.security.Model.User;
import com.esprit.security.Repository.TokenRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.Expiration}")
    private long Expiration;
    @Value("${application.security.jwt.secret-key}")
    private String secretkey;
    private final UserDetailsService userDetailsService ;
    private final TokenRepo tokenRepo;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T>  T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.
                parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken (UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);

    }
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {

        return buildToken(claims,userDetails,Expiration);
    }
    private String buildToken(
            Map<String,Object>exctraclaims,
            UserDetails userDetails,
            long jwtExpiration
    ){
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()
                .setClaims(exctraclaims)
                .setSubject(userDetailsService.loadUserByUsername(userDetails.getUsername()).getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();

    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetailsService.loadUserByUsername(userDetails.getUsername()).getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExperition(token).before(new Date());
    }

    private Date extractExperition(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void revokeAllTokens(User user) {
        var valideUserTokens = tokenRepo.findByUser(user);
        if (valideUserTokens.isEmpty()) return;
        valideUserTokens.forEach(t -> {
            t.setRevoked(true);
        });
        tokenRepo.saveAll(valideUserTokens);
    }
}
