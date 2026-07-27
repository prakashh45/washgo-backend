package com.washgo.auth.config;

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

            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            InputStream serviceAccount =
                    FirebaseConfig.class
                            .getClassLoader()
                            .getResourceAsStream("firebase/serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Could not find firebase/serviceAccountKey.json");
            }

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