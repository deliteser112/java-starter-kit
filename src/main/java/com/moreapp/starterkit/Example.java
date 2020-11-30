package com.moreapp.starterkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Example {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        final Properties properties = new Properties();
        properties.load(Example.class.getResourceAsStream("/com/moreapp/starterkit/api.properties"));
        final String endpoint = properties.getProperty("endpoint");
        final String apiKey = properties.getProperty("apiKey");
        final String customerId = properties.getProperty("customerId");

        final Request request = new Request.Builder()
                .url(endpoint + "/api/v1.0/forms/customer/" + customerId + "/folders?expand=forms")
                .get()
                .addHeader("X-API-KEY", apiKey)
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();

        if (!response.isSuccessful()) {
            final Map<String, Object> body = OBJECT_MAPPER.readValue(response.body().string(), Map.class);
            final String message = (String) body.get("message");

            System.err.println("Request failed: (" + response.code() + ") " + message);
            return;
        }

        final List<Map<String, Object>> folders = OBJECT_MAPPER.readValue(response.body().string(), List.class);

        System.out.println("Folders (and forms) for customer " + customerId + ":");

        folders.forEach((folder) -> {
            final String folderName = ((Map<String, String>) folder.get("meta")).get("name");

            System.out.println(folderName);

            final List<Map<String, Object>> forms = ((List<Map<String, Object>>) folder.get("forms"));
            forms.forEach((form) -> {
                final String formName = ((Map<String, String>) form.get("meta")).get("name");
                System.out.println(" - " + formName);
            });
        });
    }
}
