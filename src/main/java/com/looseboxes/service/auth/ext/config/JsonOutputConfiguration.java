package com.looseboxes.service.auth.ext.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.looseboxes.spring.oauth.OAuth2LoginVM;
import com.looseboxes.spring.oauth.JsonNodeToOAuth2LoginVMConverter;
import com.looseboxes.spring.oauth.OAuthResponseDeserializer;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hp
 */
@Configuration
public class JsonOutputConfiguration implements WebMvcConfigurer{
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        this.configureHttpMessageConverter(converters);
    }
    
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean public RestTemplate restTemplate() {
        return this.createConfiguredRestTemplate();
    }
    
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        return this.createConfiguredHttpMessageConverter();
    }
    
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean public ObjectMapper objectMapper() {
        return this.createConfiguredObjectMapper();
    }

    public ObjectMapper configure(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper = this.addOAuth2ResponseDeserializer(objectMapper);
        return objectMapper;
    }
    
    public void configureHttpMessageConverter(
            List<HttpMessageConverter<?>> converters) {
        
        this.configureHttpMessageConverter(converters, this.createObjectMapper());
    }
    
    public void configureHttpMessageConverter(
            List<HttpMessageConverter<?>> converters, ObjectMapper mapper) {
    
        final MappingJackson2HttpMessageConverter converter = 
                this.createConfiguredHttpMessageConverter(mapper);

        int indexToReplace = -1;
        for(int i=0; i<converters.size(); i++) {
            final HttpMessageConverter existing = converters.get(i);
            if(existing instanceof MappingJackson2HttpMessageConverter) {
                indexToReplace = i;
                break;
            }
        }
        
        if(indexToReplace != -1) {
            converters.set(indexToReplace, converter);
        }else{
            converters.add(converter);
        }
    }

    public RestTemplate createConfiguredRestTemplate() {
        return this.createConfiguredRestTemplate(this.createObjectMapper());
    }
    
    public RestTemplate createConfiguredRestTemplate(ObjectMapper objectMapper) {

        final RestTemplate restTemplate = new RestTemplate();

        // Add it to the beginning of the list so that it takes precedence 
        // over any default that Spring has registered
        restTemplate.getMessageConverters().add(0, 
                this.createConfiguredHttpMessageConverter(objectMapper));

        return restTemplate;
    }

    public MappingJackson2HttpMessageConverter createConfiguredHttpMessageConverter() {
        return this.createConfiguredHttpMessageConverter(this.createObjectMapper());
    }
    
    public MappingJackson2HttpMessageConverter createConfiguredHttpMessageConverter(
            ObjectMapper objectMapper) {
        
        objectMapper = this.configure(objectMapper);

        final MappingJackson2HttpMessageConverter converter = 
                new MappingJackson2HttpMessageConverter(objectMapper);
        
        return converter;
    }
    
    public ObjectMapper createConfiguredObjectMapper() {
        return this.configure(this.createObjectMapper());
    }
    
    private ObjectMapper createObjectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }
    
    private ObjectMapper addOAuth2ResponseDeserializer(ObjectMapper objectMapper) {
	SimpleModule module = new SimpleModule();
	module.addDeserializer(OAuth2LoginVM.class, new OAuthResponseDeserializer(jsonNodeToOAuth2LoginVMConverter()));
	objectMapper.registerModule(module);        
        return objectMapper;
    }
    
    private JsonNodeToOAuth2LoginVMConverter jsonNodeToOAuth2LoginVMConverter() {
        return new JsonNodeToOAuth2LoginVMConverter();
    }
}
