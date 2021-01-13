package com.yu.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;

@Component
public class JwtTokenController {

    /**
     * public key generated with EC256, in PEM format
     */
    @Value("${auth-service.jwt.public-key-PEM}")
    protected String publicKeyPem;

    /**
     * is enabled, token expiry time will be ignored
     */
    @Value("${people-service.options.accept-expired-auth-token}")
    protected boolean acceptExpiredAuthToken;

    private JWSVerifier verifier;

    @Autowired
    protected CurrentTimeController currentTimeController;

    @Autowired
    protected CurrentAuthController currentAuthController;

    private static final String CLAIM_FIELD_USERNAME = "username";
    private static final String CLAIM_FIELD_ROLE_LIST = "role";

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenController.class);

    @PostConstruct
    public void init(){
        Security.addProvider(new BouncyCastleProvider());

        // setup jwt tools: algorithm, header, signer, verifier
        try {
            PublicKey publicKey = this.createPublicKey();
            verifier = new ECDSAVerifier((ECPublicKey) publicKey);
        } catch (JOSEException ex) {
            logger.error("failed to create JWT toolset: {}", ex.getMessage(), ex);
            throw new RuntimeException("failed to create JWT toolset", ex);
        } catch (IOException ex) {
            logger.error("failed to create JWT toolset: {}", ex.getMessage(), ex);
            throw new RuntimeException("failed to create JWT toolset", ex);
        }
    }

    private PublicKey createPublicKey() throws IOException {
        PublicKey publicKey;
        String pemBase64 = "-----BEGIN PUBLIC KEY-----\n"
                +this.publicKeyPem
                .replaceAll("[-]*BEGIN PUBLIC KEY[-]*", "")
                .replaceAll("[-]*END PUBLIC KEY[-]*", "")
                .replace(" ", "")+"\n"
                +"-----END PUBLIC KEY-----";
//        logger.debug("public key raw: {}", pemBase64);
        PEMParser parser = new PEMParser(new StringReader(pemBase64));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(parser.readObject());
        publicKey = converter.getPublicKey(keyInfo);
//        logger.debug("public key parsed: {}", publicKey);
        return publicKey;
    }

    /**
     * @return token if verification passed, null for failure
     */
    public SignedJWT verifyToken(String accessToken) {
        try {
            SignedJWT parsedToken = SignedJWT.parse(accessToken);
//            logger.debug("token claim[username] = {}",
//                    parsedToken.getJWTClaimsSet().getClaim("username"));
            boolean isTokenValid = parsedToken.verify(this.verifier);
//            logger.debug("verify valid ? {}", isTokenValid);
            Instant currentTime = this.currentTimeController.getCurrentTime();
            if (!acceptExpiredAuthToken && currentTime.isAfter(
                parsedToken.getJWTClaimsSet().getExpirationTime().toInstant()))
            {
                // token expired
                return null;
            }
            return isTokenValid ? parsedToken : null;
        } catch (ParseException ex) {
            logger.error("failed while verifying the token", ex);
            return null;
        } catch (JOSEException ex) {
            logger.error("failed while verifying the token", ex);
            return null;
        }
    }

    public String getUsernameInToken(JWTClaimsSet claimsSet) throws ParseException {
        return claimsSet.getStringClaim(CLAIM_FIELD_USERNAME);
    }

    public String[] getRoleListInToken(JWTClaimsSet claimsSet) throws ParseException {
        String[] roleList = claimsSet.getStringArrayClaim(CLAIM_FIELD_ROLE_LIST);
        return roleList;
    }

}
