package com.washgo.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.base64:}")
    private String firebaseCredentialsBase64;

    @PostConstruct
    public void initialize() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            if (firebaseCredentialsBase64 == null || firebaseCredentialsBase64.isBlank()) {
                throw new RuntimeException(
                        "FIREBASE_CREDENTIALS_BASE64 env var / firebase.credentials.base64 property is not set");
            }

            byte[] decoded = Base64.getDecoder().decode(firebaseCredentialsBase64);
            InputStream serviceAccount = new ByteArrayInputStream(decoded);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println("=================================");
            System.out.println("Firebase Initialized Successfully");
            System.out.println("=================================");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
}