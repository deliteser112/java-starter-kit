package com.moreapp.starterkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Example {

    public static void main(String[] args) throws IOException {
        //Load the properties
        Properties properties = new Properties();
        properties.load(Example.class.getResourceAsStream("/com/moreapp/starterkit/api.properties"));
        String endpoint = properties.getProperty("endpoint");

        //Prepare the client
        OAuthRestTemplate oAuthRestTemplate = prepareClient(properties);

        //Make the API Call (get all customers)
        ResponseEntity<Map[]> response = oAuthRestTemplate.getForEntity(endpoint + "/customers", Map[].class);
        System.out.println("The response status is: " + response.getStatusCode());
        if (response.getStatusCode().value() != 200) {
            System.out.printf("Something went wrong please check the credentials and the API endpoint");
            return;
        }

        //Print the result
        System.out.println("The customers are:");
        Map[] customers = response.getBody();
        for (Map customer : customers) {
            System.out.println(" - " + customer.get("name"));
        }

        // For more api calls and the response types ee http://developer.moreapp.com/#/apidoc.
    }

    private static OAuthRestTemplate prepareClient(Properties properties) {
        //Setup the credentials
        BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
        resource.setConsumerKey(properties.getProperty("consumerKey"));
        String consumerSecret = properties.getProperty("consumerSecret");
        resource.setSharedSecret(new SharedConsumerSecretImpl(consumerSecret));
        resource.setAuthorizationHeaderRealm("more");

        OAuthRestTemplate oAuthRestTemplate = new OAuthRestTemplate(resource);

        //Add the Jackson message converter to parse the JSON response into Java objects.
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(converter);
        oAuthRestTemplate.setMessageConverters(messageConverters);

        return oAuthRestTemplate;
    }
}
