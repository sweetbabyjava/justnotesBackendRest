package justnotes.backend.rest.authentication;

//import com.example.secloud.auth.models.SecurityProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {


    @Primary
    @Bean
    public void firebaseInit() {
        InputStream inputStream = null;
        try {
            if(System.getenv("justNotes_Firebase_Config") != null
                    && !System.getenv("justNotes_Firebase_Config").isBlank()){
                inputStream = new ByteArrayInputStream(System.getenv("justNotes_Firebase_Config")
                                .getBytes(StandardCharsets.UTF_8));
            }
            else{
                inputStream = new ClassPathResource("firebase_config.json").getInputStream();
            }

        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            assert inputStream != null;
            FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials
                            .fromStream(inputStream))
                            .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            System.out.println("Firebase Initialize");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
