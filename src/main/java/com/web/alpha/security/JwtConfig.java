package com.web.alpha.security;

import com.web.alpha.auth.service.AccessTokenBlocklistService;
import com.web.alpha.config.JwtProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

	private final ResourceLoader resourceLoader;

	public JwtConfig(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public RSAPrivateKey rsaPrivateKey(JwtProperties jwtProperties)
			throws GeneralSecurityException, IOException {
		byte[] keyBytes = readPemContent(jwtProperties.privateKeyPath());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
	}

	@Bean
	public RSAPublicKey rsaPublicKey(JwtProperties jwtProperties)
			throws GeneralSecurityException, IOException {
		byte[] keyBytes = readPemContent(jwtProperties.publicKeyPath());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
	}

	@Bean
	public JwtEncoder jwtEncoder(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
		RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
				.privateKey(rsaPrivateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new NimbusJwtEncoder(new ImmutableJWKSet<>(jwkSet));
	}

	@Bean
	public JwtDecoder jwtDecoder(
			RSAPublicKey rsaPublicKey,
			JwtProperties jwtProperties,
			AccessTokenBlocklistService accessTokenBlocklistService
	) {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey)
				.signatureAlgorithm(SignatureAlgorithm.RS256)
				.build();

		OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(jwtProperties.issuer()),
				new BlocklistedJwtValidator(accessTokenBlocklistService)
		);
		jwtDecoder.setJwtValidator(validator);
		return jwtDecoder;
	}

	private byte[] readPemContent(String location) throws IOException {
		Resource resource = resourceLoader.getResource(location);
		if (!resource.exists()) {
			throw new IllegalStateException("JWT key resource not found: " + location);
		}

		String pem;
		try (InputStream inputStream = resource.getInputStream()) {
			pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
		String base64Content = pem
				.replaceAll("-----BEGIN ([A-Z ]+)-----", "")
				.replaceAll("-----END ([A-Z ]+)-----", "")
				.replaceAll("\\s", "");

		return Base64.getDecoder().decode(base64Content);
	}
}
