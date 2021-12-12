package justnotes.backend.rest.translation;

import com.google.cloud.translate.v3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TranslationService {

    public String translateText(String projectId, String targetLanguage, String text)
            throws IOException {

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");

            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(text)
                            .build();

            TranslateTextResponse response = client.translateText(request);

            // concat the responses to a string
            String result = "";
            for (Translation translation : response.getTranslationsList()) {
                result = result.concat(translation.getTranslatedText());
            }
            return result;
        }
    }
}
