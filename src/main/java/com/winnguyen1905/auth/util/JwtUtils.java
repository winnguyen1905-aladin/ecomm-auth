package com.winnguyen1905.auth.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.winnguyen1905.auth.common.constant.RegionPartition;
import com.winnguyen1905.auth.core.model.response.CustomUserDetails;
import com.winnguyen1905.auth.core.service.GeoLocationService;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class JwtUtils {
  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

  private final GeoLocationService geoLocationService;

  @Autowired
  private JwtEncoder jwtEncoder;

  @Value("${jwt.access_token-validity-in-seconds}")
  private String jwtAccessTokenExpiration;

  @Value("${jwt.refresh_token-validity-in-seconds}")
  private String jwtRefreshTokenExpiration;

  public JwtClaimsSet createJwtClaimsSet(CustomUserDetails userDetails, Instant now, Instant validity,
      RegionPartition region) {
    // List<PermissionVm> permissions =
    // userDetails.role() instanceof RoleVm
    // ? userDetails.role().permissions()
    // : new ArrayList<>();
    return JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(validity)
        .subject(userDetails.id().toString())
        .claim("username", userDetails.username())
        .claim("role", userDetails.role())
        .claim("region", region)
        .build();
  }
  private String getClientIp(ServerHttpRequest request) {
    try {
      String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
      if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
          return xForwardedFor.split(",")[0].trim();
      }
      var remoteAddress = request.getRemoteAddress();
      if (remoteAddress != null && remoteAddress.getAddress() != null) {
        return remoteAddress.getAddress().getHostAddress();
      }
      return "unknown";
    } catch (Exception e) {
      // If we can't access headers (they might be read-only), return a default
      return "unknown";
    }
  }

  public TokenPair createTokenPair(CustomUserDetails userDetails, ServerWebExchange exchange) {
    try {
      var request = exchange.getRequest();
      String ipAddress = getClientIp(request);
      RegionPartition region = geoLocationService.getRegionFromIp(ipAddress);
      return createTokenPairWithRegion(userDetails, region);
    } catch (Exception e) {
      log.warn("Failed to get client IP from exchange, using default region: {}", e.getMessage());
      // Fallback to default region if we can't access the exchange properly
      return createTokenPairWithRegion(userDetails, RegionPartition.US);
    }
  }

  public TokenPair createTokenPairWithRegion(CustomUserDetails userDetails, RegionPartition region) {
    Instant now = Instant.now(),
        accessTokenValidity = now.plus(Long.parseLong(jwtAccessTokenExpiration), ChronoUnit.SECONDS),
        refreshTokenValidity = now.plus(Long.parseLong(jwtRefreshTokenExpiration), ChronoUnit.SECONDS);
    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    String accessToken = jwtEncoder.encode(JwtEncoderParameters
        .from(jwsHeader, createJwtClaimsSet(userDetails, now, accessTokenValidity, region))).getTokenValue();
    String refreshToken = jwtEncoder.encode(JwtEncoderParameters
        .from(jwsHeader, createJwtClaimsSet(userDetails, now, refreshTokenValidity, region))).getTokenValue();
    return TokenPair.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken).build();
  }
}
