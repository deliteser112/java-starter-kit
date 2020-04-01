package com.moreapp.starterkit.hook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class HookWebService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        //Load the properties
        Properties properties = new Properties();
        properties.load(HookWebService.class.getResourceAsStream("/com/moreapp/starterkit/api.properties"));
        String endpoint = properties.getProperty("endpoint");

        port(3000);
        post("/test", "application/json", (request, response) -> "{ status : 'VALID'}");
        get("/health", "application/json", (request, response) -> "{ status : 'OK'}");
        post("/handle", "application/json", (request, response) -> {
            System.out.println(request.body());

            HookRequest hookRequest = objectMapper.readValue(request.body(), HookRequest.class);

            Integer customerId = (Integer) ((Map) hookRequest.getRegistration().get("info")).get("customerId");

            downloadFiles(endpoint, hookRequest, customerId);

            System.out.println("Handle");
            return new HookResponse(HookResponse.Status.SUCCESS, null);
        }, HookWebService::toJson);
    }

    private static String toJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    private static void downloadFiles(String endpoint, HookRequest hookRequest, Integer customerId) throws IOException {
        List mailStatuses = (List) hookRequest.getRegistration().get("mailStatuses");
        if (mailStatuses == null) {
            return;
        }
        for (Object mailStatus : mailStatuses) {
            String pdfFileId = (String) ((Map) mailStatus).get("pdfFileId");

            downloadFile(endpoint, hookRequest, customerId, pdfFileId);
        }
    }

    private static void downloadFile(String endpoint, HookRequest hookRequest, Integer customerId, String pdfFileId) throws IOException {
        String url = endpoint + "/customers/" + customerId + "/resources/" + pdfFileId + "/download/direct";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.set("X-MORE-SEAL", hookRequest.getSeal());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> moreResponse = new RestTemplate().exchange(url, HttpMethod.GET, entity, byte[].class);

        if (moreResponse.getStatusCode() == HttpStatus.OK) {
            Files.write(Paths.get("registration-file-" + pdfFileId + ".pdf"), moreResponse.getBody());
        }
    }
}
