package it.bicocca.eduquest.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String testSecret = "RainieriRanicaTurconiWang2026IngegneriaDelSoftwareKey"; 
    private final int testExpiration = 6700000; 

    @BeforeEach
    void setUp() { //automatic injection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpiration);
    }

    @Test
    void testGenerateToken_Success() {
        long userId = 1L;

        String token = jwtUtils.generateToken(userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGetUserIdFromToken_Success() {
        long userId = 11L;
        
        String token = jwtUtils.generateToken(userId);
        
        long extractedId = jwtUtils.getUserIdFromToken(token);

        assertEquals(userId, extractedId);
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtUtils.generateToken(111L);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidSignature() {
        String token = jwtUtils.generateToken(111L);
        
        String invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "FalseSign";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_Malformed() {
        assertFalse(jwtUtils.validateToken("token.not.valid"));
        assertFalse(jwtUtils.validateToken("grbg"));
    }

    @Test
    void testValidateToken_EmptyOrNull() {
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    void testValidateToken_Expired() {
        JwtUtils expiredJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtExpirationMs", -1000); 

        String expiredToken = expiredJwtUtils.generateToken(1L);
        
        assertFalse(jwtUtils.validateToken(expiredToken));
    }
}