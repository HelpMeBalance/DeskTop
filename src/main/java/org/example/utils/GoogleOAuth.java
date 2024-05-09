package org.example.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Objects;

public class GoogleOAuth {
    public static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String CLIENT_SECRET_FILE = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public GoogleAuthorizationCodeFlow createFlow() throws IOException {
        InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(GoogleOAuth.class.getResourceAsStream(CLIENT_SECRET_FILE))
        );
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY,
                clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(),
                Collections.singletonList("https://www.googleapis.com/auth/userinfo.email"))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }
}
