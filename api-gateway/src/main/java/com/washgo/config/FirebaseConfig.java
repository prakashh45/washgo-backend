package com.washgo.config;

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
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount = resolveServiceAccount();

                if (serviceAccount == null) {
                    throw new RuntimeException(
                            "Firebase credentials are not configured. Set FIREBASE_CREDENTIALS_BASE64 or include firebase-service-account.json");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }

            System.out.println("Firebase initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    private InputStream resolveServiceAccount() {
        if (firebaseCredentialsBase64 != null && !firebaseCredentialsBase64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(firebaseCredentialsBase64.trim());
            return new ByteArrayInputStream(decoded);
        }

        return FirebaseConfig.class.getClassLoader()
                .getResourceAsStream("firebase-service-account.json");
    }
    }
