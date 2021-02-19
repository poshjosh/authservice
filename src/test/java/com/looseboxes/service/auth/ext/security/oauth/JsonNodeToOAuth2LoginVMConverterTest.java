package com.looseboxes.service.auth.ext.security.oauth;

import com.looseboxes.spring.oauth.JsonNodeToOAuth2LoginVMConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.spring.oauth.OAuth2LoginVM;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
//import static org.junit.Assert.*;

/**
 * @author hp
 */
public class JsonNodeToOAuth2LoginVMConverterTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String clientId = "google";
    private final String accessToken = "ya29.a0AfH6SMCQrdjNMETtCHLx2igipl_HBFxx_s-tEzER9iSFQtyzFc6CgvVDVVPa6UJg1ewmLwNR5g5oFeP1nJsjoLMvu5_xzQPTr--iSBCzc24QIlcm9_Oi6DZwO8Qnpfye--mF5--LQ5HYr2ihm5NravdI4lbB_5fyzLw";
    private final String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjRiODNmMTgwMjNhODU1NTg3Zjk0MmU3NTEwMjI1MTEyMDg4N2Y3MjUiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTU2NTg1NDcxMTIxMTM0NDc4NTYiLCJlbWFpbCI6Im1haWwub3dvYmxvd0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6ImpES3Q1N3Z0YzlNbFRLUXR5T21ZX0EiLCJuYW1lIjoib3dvYmxvdyBtb25leSIsInBpY3R1cmUiOiJodHRwczovL2xoNi5nb29nbGV1c2VyY29udGVudC5jb20vLWFVYXdMazIxY1BzL0FBQUFBQUFBQUFJL0FBQUFBQUFBQUFBL0FNWnV1Y24zUjZoc1lVZzhWRXoxYkYtLWRPVlhERk5tNUEvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6Im93b2Jsb3ciLCJmYW1pbHlfbmFtZSI6Im1vbmV5IiwibG9jYWxlIjoiZW4tR0IiLCJpYXQiOjE2MDA0NDUwNDcsImV4cCI6MTYwMDQ0ODY0N30.A2Br3OMxuMjdJb2sHrNsh7UFFiIA-3x6bSmXcTJxrRLv6_zwL24vCx0rPjyc5Tk57fSv1YPSvF0WJ2hkDA6OL0e0p4ZEGaus1ulOX2r_cPqsG6B6WyXw_S2Ff0-Mt3y4bsSNuAguKxMYSvGHq0rH0qalB4UNRQ9ntrf13zCUOSab5ZqMTKHLkse6FZaj17-4Dn0tTZSv2OzGkvxS8w4vtn2XpdumabMMdITj5HgF3SSO5Bu4hhF03WXG1knu3h_IJxrliBVnrkBj15XN4QWcYNssLxloFn20bY8FvYXVcEBGVH7PAInZSkaRY2ViXHzHDHhgzk7L3GfAyHhIfXsEBw";
    private final Integer expiresIn = 3599;
    private final String tokenType = "Bearer";
    private final List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile", "openid");
    private final String refreshToken = "1//04Ph9TlQ8S5GCCgYIARAAGAQSNwF-L9IrPhrFHhpNfaQ8x1VXIYApiorgpkaeg_XH4RLk3ntyF-CtcheM5p_r6N5RWqjToF4OK7M";
    
    private final boolean debug = false;
    
    public JsonNodeToOAuth2LoginVMConverterTest() { }

    @Test
    public void giventValidJsonNode_shouldReturnValidResult() throws JsonProcessingException {
        System.out.println("giventValidJsonNode_shouldReturnValidResult");
        this.shouldReturnValidResult(givenValidJsonNode());
    }

    @Test
    public void givenJsonNodeWithNamedAccessToken_shouldReturnValidResult() throws JsonProcessingException {
        System.out.println("givenJsonNodeWithNamedAccessToken_shouldReturnValidResult");
        this.shouldReturnValidResult(givenJsonNodeWithNamedAccessToken("accessToken"));
    }

    @Test
    public void givenJsonNode_havingCommaDelimitedTextAsScope_shouldReturnValidResult() throws JsonProcessingException {
        System.out.println("givenJsonNode_havingCommaDelimitedTextAsScope_shouldReturnValidResult");
        this.shouldReturnValidResult(givenJsonNode_havingDelimitedTextAsScope("  , "));
    }

    @Test
    public void givenJsonNode_havingSpaceDelimitedTextAsScope_shouldReturnValidResult() throws JsonProcessingException {
        System.out.println("givenJsonNode_havingSpaceDelimitedTextAsScope_shouldReturnValidResult");
        this.shouldReturnValidResult(givenJsonNode_havingDelimitedTextAsScope("        "));
    }
    
    @Test
    public void givenJsonNodeWithNullClientId_shouldThrowException() throws JsonProcessingException {
        System.out.println("givenJsonNodeWithNullClientId_shouldThrowException");
        this.shouldThrowException(givenJsonNodeWithNullClientId());
    }
    
    private void shouldReturnValidResult(JsonNode node) throws JsonProcessingException {
        if(debug) System.out.println(" Input: " + node.toString());
        JsonNodeToOAuth2LoginVMConverter instance = new JsonNodeToOAuth2LoginVMConverter();
        OAuth2LoginVM result = instance.convert(node);
        if(debug) System.out.println("Result: " + result);
        assertThat(result.getAccess_token(), is(this.accessToken));
        assertThat(result.getClient_id(), is(this.clientId));
        assertThat(result.getExpires_in(), is(this.expiresIn));
        assertThat(result.getScopes(), is(this.scopes));
    }
    
    private void shouldThrowException(JsonNode node) throws JsonProcessingException {
        if(debug) System.out.println(" Input: " + node.toString());
        JsonNodeToOAuth2LoginVMConverter instance = new JsonNodeToOAuth2LoginVMConverter();
        try{
            OAuth2LoginVM result = instance.convert(node);
            if(debug) System.out.println("Result: " + result);
            fail("Should throw exception but completed execution");
        }catch(RuntimeException expected) { }
    }
    
    private JsonNode givenValidJsonNode() throws JsonProcessingException{
        return objectMapper.readTree(this.getValidJsonText());
    }

    private JsonNode givenJsonNodeWithNamedAccessToken(String name) throws JsonProcessingException{
        String text = this.getJsonText(accessToken, clientId, expiresIn, scopes);
        text = text.replace("access_token", name);
        return objectMapper.readTree(text);
    }

    private JsonNode givenJsonNode_havingDelimitedTextAsScope(String separator) throws JsonProcessingException{
        return objectMapper.readTree(this.getJsonText(accessToken, clientId, expiresIn, 
                scopes.stream().collect(Collectors.joining(separator))));
    }
    
    private JsonNode givenJsonNodeWithNullClientId() throws JsonProcessingException{
        return objectMapper.readTree(this.getJsonText(accessToken, null, expiresIn, scopes));
    }
    
    private String getValidJsonText() throws JsonProcessingException{
        return this.getJsonText(accessToken, clientId, expiresIn, scopes);
    }

    private String getJsonText(String accessToken, String clientId, Integer expiresIn, Object scopes) throws JsonProcessingException{
        Map map = new HashMap();
        map.put("client_id", clientId);
        map.put("access_token", accessToken);
        map.put("id_token", idToken);
        map.put("expires_in", expiresIn);
        map.put("token_type", tokenType);
        map.put("scope", scopes);
        map.put("refresh_token", refreshToken);
        return objectMapper.writeValueAsString(map);
    }
}
