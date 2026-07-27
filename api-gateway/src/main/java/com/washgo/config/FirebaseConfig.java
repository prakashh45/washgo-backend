package com.washgo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount =
                        FirebaseConfig.class.getClassLoader()
                                .getResourceAsStream("firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Could not find firebase-service-account.json");
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
    }
